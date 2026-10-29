package com.example.homestay.web;

import com.example.homestay.domain.BookingOrder;
import com.example.homestay.domain.OrderStatus;
import com.example.homestay.exception.BusinessException;
import com.example.homestay.repository.ReviewRepository;
import com.example.homestay.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class OrderController {

    private final BookingService bookingService;
    private final ReviewRepository reviewRepository;

    public OrderController(BookingService bookingService, ReviewRepository reviewRepository) {
        this.bookingService = bookingService;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/orders")
    public String list(@RequestParam(required = false) OrderStatus status, Model model) {
        model.addAttribute("orders", bookingService.list(status));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", OrderStatus.values());
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String detail(@PathVariable Long id, Model model) {
        BookingOrder order = bookingService.get(id);
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("hasReview", reviewRepository.existsByOrderId(id));
        return "order-detail";
    }

    @PostMapping("/orders/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            RedirectAttributes redirectAttributes) {
        try {
            bookingService.changeStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "订单状态已更新为「" + status.getLabel() + "」");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/orders/" + id;
    }
}
