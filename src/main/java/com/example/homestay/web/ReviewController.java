package com.example.homestay.web;

import com.example.homestay.domain.BookingOrder;
import com.example.homestay.domain.OrderStatus;
import com.example.homestay.domain.Review;
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

@Controller
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final BookingService bookingService;

    public ReviewController(ReviewRepository reviewRepository, BookingService bookingService) {
        this.reviewRepository = reviewRepository;
        this.bookingService = bookingService;
    }

    @GetMapping("/reviews")
    public String list(Model model) {
        model.addAttribute("reviews", reviewRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("averageRating", Math.round(reviewRepository.averageVisibleRating() * 10.0) / 10.0);
        return "reviews";
    }

    @GetMapping("/reviews/new")
    public String createPage(@RequestParam Long orderId, Model model) {
        BookingOrder order = bookingService.get(orderId);
        if (reviewRepository.existsByOrderId(orderId)) {
            return "redirect:/orders/" + orderId;
        }
        model.addAttribute("order", order);
        return "review-form";
    }

    @PostMapping("/reviews")
    public String create(
            @RequestParam Long orderId,
            @RequestParam int rating,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {
        BookingOrder order = bookingService.get(orderId);
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new BusinessException("只有已完成订单可以评价");
        }
        if (rating < 1 || rating > 5) {
            throw new BusinessException("评分必须在1到5分之间");
        }
        if (content == null || content.trim().length() < 5) {
            throw new BusinessException("评价内容至少填写5个字符");
        }
        if (reviewRepository.existsByOrderId(orderId)) {
            throw new BusinessException("该订单已经评价");
        }

        Review review = new Review();
        review.setOrder(order);
        review.setRating(rating);
        review.setContent(content.trim());
        reviewRepository.save(review);
        redirectAttributes.addFlashAttribute("successMessage", "评价已发布");
        return "redirect:/reviews";
    }

    @PostMapping("/reviews/{id}/reply")
    public String reply(
            @PathVariable Long id,
            @RequestParam String reply,
            RedirectAttributes redirectAttributes) {
        Review review = reviewRepository.findWithOrderById(id)
                .orElseThrow(() -> new BusinessException("评价不存在"));
        review.setReply(reply == null || reply.isBlank() ? null : reply.trim());
        reviewRepository.save(review);
        redirectAttributes.addFlashAttribute("successMessage", "商家回复已保存");
        return "redirect:/reviews";
    }

    @PostMapping("/reviews/{id}/visibility")
    public String toggleVisibility(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new BusinessException("评价不存在"));
        review.setVisible(!review.isVisible());
        reviewRepository.save(review);
        redirectAttributes.addFlashAttribute("successMessage",
                review.isVisible() ? "评价已设为公开" : "评价已隐藏");
        return "redirect:/reviews";
    }
}
