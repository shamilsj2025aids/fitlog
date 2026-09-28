package com.fitlog.controller;

import com.fitlog.dto.DailySummaryResponse;
import com.fitlog.dto.WeeklyTrendResponse;
import com.fitlog.service.SummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/summaries")
@Tag(name = "Summaries & Trends", description = "Core Features 3 & 4: Daily calorie balance and weekly workout trends")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/daily")
    @Operation(summary = "Core Feature 3: View a daily summary of calories in vs calories out")
    public ResponseEntity<DailySummaryResponse> getDailySummary(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(summaryService.getDailySummary(userId, date));
    }

    @GetMapping("/weekly-trend")
    @Operation(summary = "Core Feature 4: View a weekly trend of workouts completed (Enforces: Requires at least one entry for the week)")
    public ResponseEntity<WeeklyTrendResponse> getWeeklyTrend(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate) {
        return ResponseEntity.ok(summaryService.getWeeklyTrend(userId, weekStartDate));
    }
}
