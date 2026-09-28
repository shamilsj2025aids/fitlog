package com.fitlog.controller;

import com.fitlog.dto.DashboardOverviewResponse;
import com.fitlog.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Idea to Go Further: Dashboard showing total system counts and activity overview")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    @Operation(summary = "Idea to Go Further: Get system-wide dashboard metrics and total counts")
    public ResponseEntity<DashboardOverviewResponse> getDashboardOverview() {
        return ResponseEntity.ok(dashboardService.getOverview());
    }
}
