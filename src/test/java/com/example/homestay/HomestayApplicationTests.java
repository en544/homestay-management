package com.example.homestay;

import com.example.homestay.domain.BookingOrder;
import com.example.homestay.domain.OrderStatus;
import com.example.homestay.domain.Room;
import com.example.homestay.domain.RoomStatus;
import com.example.homestay.repository.BookingOrderRepository;
import com.example.homestay.repository.HomestayRepository;
import com.example.homestay.repository.RoomRepository;
import com.example.homestay.service.BookingService;
import com.example.homestay.web.form.BookingForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:homestay-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.h2.console.enabled=false"
})
@AutoConfigureMockMvc
class HomestayApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingOrderRepository orderRepository;

    @Autowired
    private HomestayRepository homestayRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void loginPageRenders() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("欢迎回来")));
    }

    @Test
    void protectedPageRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/homestays", "/homestays/1", "/rooms", "/orders", "/reviews", "/bookings/new"})
    void adminPagesRender(String path) throws Exception {
        mockMvc.perform(get(path).session(adminSession()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    void orderDetailAndEditPagesRender() throws Exception {
        BookingOrder order = orderRepository.findAllByOrderByCreatedAtDesc().get(0);
        mockMvc.perform(get("/orders/{id}", order.getId()).session(adminSession()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
        mockMvc.perform(get("/rooms/1/edit").session(adminSession()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
        mockMvc.perform(get("/homestays/1/edit").session(adminSession()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    void bookingCreationReservesInventoryByDate() {
        Room room = roomRepository.findAllByOrderByPriceAsc().get(0);
        LocalDate checkIn = LocalDate.now().plusDays(20);
        LocalDate checkOut = checkIn.plusDays(2);

        BookingForm form = new BookingForm();
        form.setRoomId(room.getId());
        form.setGuestName("课程测试住客");
        form.setGuestPhone("13800138888");
        form.setCheckIn(checkIn);
        form.setCheckOut(checkOut);
        form.setRoomCount(1);
        form.setGuests(2);
        form.setNote("自动化测试订单");

        BookingOrder order = bookingService.create(form);

        assertThat(order.getId()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getRoom().getId()).isEqualTo(room.getId());
        assertThat(order.getNights()).isEqualTo(2);
        assertThat(order.getTotalAmount())
                .isEqualByComparingTo(room.getPrice().multiply(BigDecimal.valueOf(2)));
        assertThat(orderRepository.findById(order.getId())).isPresent();

        bookingService.changeStatus(order.getId(), OrderStatus.CONFIRMED);
        bookingService.changeStatus(order.getId(), OrderStatus.CHECKED_IN);
        bookingService.changeStatus(order.getId(), OrderStatus.COMPLETED);
        assertThat(orderRepository.findById(order.getId()).orElseThrow().getStatus())
                .isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void demoDataIsInitialized() {
        assertThat(homestayRepository.count()).isGreaterThanOrEqualTo(3);
        assertThat(roomRepository.count()).isGreaterThanOrEqualTo(6);
        assertThat(roomRepository.countByStatus(RoomStatus.AVAILABLE)).isGreaterThan(0);
    }

    private MockHttpSession adminSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("adminUser", "测试管理员");
        return session;
    }
}
