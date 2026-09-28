package com.fitlog.service;

import com.fitlog.dto.DailySummaryResponse;
import com.fitlog.dto.WeeklyTrendResponse;

import java.time.LocalDate;

public interface SummaryService {
    DailySummaryResponse getDailySummary(Long userId, LocalDate date);
    WeeklyTrendResponse getWeeklyTrend(Long userId, LocalDate weekStartDate);
}
