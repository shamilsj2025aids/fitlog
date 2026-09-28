package com.fitlog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class GoalRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Target workout count is required")
    @Positive(message = "Target workout count must be greater than zero")
    private Integer targetWorkoutCount;

    private LocalDate weekStartDate;

    public GoalRequest() {
    }

    public GoalRequest(Long userId, Integer targetWorkoutCount, LocalDate weekStartDate) {
        this.userId = userId;
        this.targetWorkoutCount = targetWorkoutCount;
        this.weekStartDate = weekStartDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getTargetWorkoutCount() {
        return targetWorkoutCount;
    }

    public void setTargetWorkoutCount(Integer targetWorkoutCount) {
        this.targetWorkoutCount = targetWorkoutCount;
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }
}
