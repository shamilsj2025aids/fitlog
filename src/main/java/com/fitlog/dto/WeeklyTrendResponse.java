package com.fitlog.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeeklyTrendResponse {

    private Long userId;
    private String userName;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private Integer totalWorkoutsCompleted;
    private Integer totalDurationMinutes;
    private Double totalCaloriesBurnt;
    private Double totalCaloriesConsumed;
    private Double netWeeklyCalories;
    private List<DailyBreakdown> dailyBreakdowns = new ArrayList<>();
    private GoalResponse goalSummary;

    public WeeklyTrendResponse() {
    }

    public WeeklyTrendResponse(Long userId, String userName, LocalDate weekStartDate, LocalDate weekEndDate, Integer totalWorkoutsCompleted, Integer totalDurationMinutes, Double totalCaloriesBurnt, Double totalCaloriesConsumed, Double netWeeklyCalories, List<DailyBreakdown> dailyBreakdowns, GoalResponse goalSummary) {
        this.userId = userId;
        this.userName = userName;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.totalWorkoutsCompleted = totalWorkoutsCompleted;
        this.totalDurationMinutes = totalDurationMinutes;
        this.totalCaloriesBurnt = totalCaloriesBurnt;
        this.totalCaloriesConsumed = totalCaloriesConsumed;
        this.netWeeklyCalories = netWeeklyCalories;
        this.dailyBreakdowns = dailyBreakdowns;
        this.goalSummary = goalSummary;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public LocalDate getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(LocalDate weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    public Integer getTotalWorkoutsCompleted() {
        return totalWorkoutsCompleted;
    }

    public void setTotalWorkoutsCompleted(Integer totalWorkoutsCompleted) {
        this.totalWorkoutsCompleted = totalWorkoutsCompleted;
    }

    public Integer getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public void setTotalDurationMinutes(Integer totalDurationMinutes) {
        this.totalDurationMinutes = totalDurationMinutes;
    }

    public Double getTotalCaloriesBurnt() {
        return totalCaloriesBurnt;
    }

    public void setTotalCaloriesBurnt(Double totalCaloriesBurnt) {
        this.totalCaloriesBurnt = totalCaloriesBurnt;
    }

    public Double getTotalCaloriesConsumed() {
        return totalCaloriesConsumed;
    }

    public void setTotalCaloriesConsumed(Double totalCaloriesConsumed) {
        this.totalCaloriesConsumed = totalCaloriesConsumed;
    }

    public Double getNetWeeklyCalories() {
        return netWeeklyCalories;
    }

    public void setNetWeeklyCalories(Double netWeeklyCalories) {
        this.netWeeklyCalories = netWeeklyCalories;
    }

    public List<DailyBreakdown> getDailyBreakdowns() {
        return dailyBreakdowns;
    }

    public void setDailyBreakdowns(List<DailyBreakdown> dailyBreakdowns) {
        this.dailyBreakdowns = dailyBreakdowns;
    }

    public GoalResponse getGoalSummary() {
        return goalSummary;
    }

    public void setGoalSummary(GoalResponse goalSummary) {
        this.goalSummary = goalSummary;
    }
}
