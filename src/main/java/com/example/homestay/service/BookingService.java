package com.example.homestay.service;

import com.example.homestay.domain.BookingOrder;
import com.example.homestay.domain.OrderStatus;
import com.example.homestay.domain.Room;
import com.example.homestay.domain.RoomStatus;
import com.example.homestay.exception.BusinessException;
import com.example.homestay.repository.BookingOrderRepository;
import com.example.homestay.repository.RoomRepository;
import com.example.homestay.web.form.BookingForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class BookingService {

    private static final EnumSet<OrderStatus> RESERVING_STATUSES =
            EnumSet.of(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.CHECKED_IN);

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PENDING, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.CHECKED_IN, OrderStatus.CANCELLED),
            OrderStatus.CHECKED_IN, EnumSet.of(OrderStatus.COMPLETED),
            OrderStatus.COMPLETED, EnumSet.noneOf(OrderStatus.class),
            OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class)
    );

    private final BookingOrderRepository orderRepository;
    private final RoomRepository roomRepository;
    private final AvailabilityService availabilityService;

    public BookingService(
            BookingOrderRepository orderRepository,
            RoomRepository roomRepository,
            AvailabilityService availabilityService) {
        this.orderRepository = orderRepository;
        this.roomRepository = roomRepository;
        this.availabilityService = availabilityService;
    }

    @Transactional
    public BookingOrder create(BookingForm form) {
        validateDates(form.getCheckIn(), form.getCheckOut());
        Room room = roomRepository.findByIdForUpdate(form.getRoomId())
                .orElseThrow(() -> new BusinessException("房型不存在"));

        if (!room.getHomestay().isActive() || room.getStatus() != RoomStatus.AVAILABLE) {
            throw new BusinessException("该房型当前不可预订");
        }
        if (form.getRoomCount() > room.getTotalRooms()) {
            throw new BusinessException("预订间数超过可售房量");
        }
        if (form.getGuests() > form.getRoomCount() * room.getCapacity()) {
            throw new BusinessException("入住人数超过所选房间的接待上限");
        }

        int reserved = orderRepository.sumReservedRooms(
                room.getId(), form.getCheckIn(), form.getCheckOut(), RESERVING_STATUSES);
        if (reserved + form.getRoomCount() > room.getTotalRooms()) {
            throw new BusinessException("所选日期房量不足，请调整日期或间数");
        }

        long nights = form.getCheckOut().toEpochDay() - form.getCheckIn().toEpochDay();
        BigDecimal amount = room.getPrice()
                .multiply(BigDecimal.valueOf(nights))
                .multiply(BigDecimal.valueOf(form.getRoomCount()));

        BookingOrder order = new BookingOrder();
        order.setOrderNo(generateOrderNo());
        order.setRoom(room);
        order.setGuestName(form.getGuestName().trim());
        order.setGuestPhone(form.getGuestPhone().trim());
        order.setCheckIn(form.getCheckIn());
        order.setCheckOut(form.getCheckOut());
        order.setRoomCount(form.getRoomCount());
        order.setGuests(form.getGuests());
        order.setTotalAmount(amount);
        order.setNote(form.getNote());
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    @Transactional
    public void changeStatus(Long orderId, OrderStatus target) {
        BookingOrder order = orderRepository.findWithRoomById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (!ALLOWED_TRANSITIONS.get(order.getStatus()).contains(target)) {
            throw new BusinessException("当前订单状态不能执行此操作");
        }

        if (target == OrderStatus.CONFIRMED) {
            int reserved = orderRepository.sumReservedRooms(
                    order.getRoom().getId(), order.getCheckIn(), order.getCheckOut(), RESERVING_STATUSES);
            if (reserved > order.getRoom().getTotalRooms()) {
                throw new BusinessException("当前房量不足，无法确认订单");
            }
        }
        order.setStatus(target);
    }

    @Transactional(readOnly = true)
    public List<BookingOrder> list(OrderStatus status) {
        return status == null
                ? orderRepository.findAllByOrderByCreatedAtDesc()
                : orderRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Transactional(readOnly = true)
    public BookingOrder get(Long id) {
        return orderRepository.findWithRoomById(id)
                .orElseThrow(() -> new BusinessException("订单不存在"));
    }

    public BookingOrder getOrNull(Long id) {
        return orderRepository.findWithRoomById(id).orElse(null);
    }

    public int availableRooms(Room room, LocalDate checkIn, LocalDate checkOut) {
        return availabilityService.availableRooms(room, checkIn, checkOut);
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new BusinessException("请选择入住和离店日期");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new BusinessException("入住日期不能早于今天");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new BusinessException("离店日期必须晚于入住日期");
        }
        if (checkOut.toEpochDay() - checkIn.toEpochDay() > 30) {
            throw new BusinessException("单次预订最长30晚");
        }
    }

    private String generateOrderNo() {
        return "HS" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + ThreadLocalRandom.current().nextInt(100000, 999999);
    }
}
