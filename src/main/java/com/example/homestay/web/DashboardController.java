package com.example.homestay.web;

import com.example.homestay.domain.OrderStatus;
import com.example.homestay.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("stats", dashboardService.snapshot());
        model.addAttribute("weeklyOccupancy", dashboardService.weeklyOccupancy());
        model.addAttribute("orderStatuses", OrderStatus.values());
        return "dashboard";
    }
}
