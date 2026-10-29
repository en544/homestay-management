package com.example.homestay.web;

import com.example.homestay.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Value("${app.admin.display-name}")
    private String displayName;

    @ModelAttribute("adminName")
    public String adminName() {
        return displayName;
    }

    @ModelAttribute("today")
    public LocalDate today() {
        return LocalDate.now();
    }

    @ExceptionHandler({BusinessException.class, IllegalArgumentException.class})
    public String handleBusinessException(RuntimeException ex, Model model) {
        model.addAttribute("errorTitle", "操作没有完成");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }
}
