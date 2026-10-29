package com.example.homestay.web;

import com.example.homestay.domain.BookingOrder;
import com.example.homestay.domain.Room;
import com.example.homestay.domain.RoomStatus;
import com.example.homestay.exception.BusinessException;
import com.example.homestay.repository.RoomRepository;
import com.example.homestay.service.AvailabilityService;
import com.example.homestay.service.BookingService;
import com.example.homestay.web.form.BookingForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class BookingController {

    private final RoomRepository roomRepository;
    private final BookingService bookingService;
    private final AvailabilityService availabilityService;

    public BookingController(
            RoomRepository roomRepository,
            BookingService bookingService,
            AvailabilityService availabilityService) {
        this.roomRepository = roomRepository;
        this.bookingService = bookingService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/bookings/new")
    public String createPage(@RequestParam(required = false) Long roomId, Model model) {
        BookingForm form = new BookingForm();
        form.setCheckIn(LocalDate.now());
        form.setCheckOut(LocalDate.now().plusDays(1));
        form.setRoomId(roomId);
        populateModel(model, form);
        return "booking-form";
    }

    @PostMapping("/bookings")
    public String create(
            @Valid @ModelAttribute("bookingForm") BookingForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateModel(model, form);
            return "booking-form";
        }
        try {
            BookingOrder order = bookingService.create(form);
            redirectAttributes.addFlashAttribute("successMessage", "预订已创建，请确认订单信息");
            return "redirect:/orders/" + order.getId();
        } catch (BusinessException ex) {
            model.addAttribute("formError", ex.getMessage());
            populateModel(model, form);
            return "booking-form";
        }
    }

    private void populateModel(Model model, BookingForm form) {
        List<Room> rooms = roomRepository.findBookableByStatus(RoomStatus.AVAILABLE);
        model.addAttribute("rooms", rooms);
        model.addAttribute("selectedRoom", form.getRoomId() == null
                ? null
                : roomRepository.findWithHomestayById(form.getRoomId()).orElse(null));
        model.addAttribute("bookingForm", form);
        if (form.getRoomId() != null && form.getCheckIn() != null && form.getCheckOut() != null) {
            roomRepository.findWithHomestayById(form.getRoomId()).ifPresent(room -> model.addAttribute(
                    "selectedAvailable",
                    availabilityService.availableRooms(room, form.getCheckIn(), form.getCheckOut())));
        }
    }
}
