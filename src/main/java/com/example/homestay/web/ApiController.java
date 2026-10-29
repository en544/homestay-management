package com.example.homestay.web;

import com.example.homestay.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final DashboardService dashboardService;

    public ApiController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "application", "民宿预订与房态管理系统",
                "time", LocalDateTime.now().toString()
        );
    }

    @GetMapping("/dashboard")
    public DashboardService.DashboardSnapshot dashboard() {
        return dashboardService.snapshot();
    }
}
