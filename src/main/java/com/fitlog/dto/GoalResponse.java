package com.fitlog.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GoalResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Integer targetWorkoutCount;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private String status;
    private Integer achievedWorkoutCount;
    private Integer remainingWorkouts;
    private Boolean isGoalAchieved;
    private LocalDateTime createdAt;

    public GoalResponse() {
    }

    public GoalResponse(Long id, Long userId, String userName, Integer targetWorkoutCount, LocalDate weekStartDate, LocalDate weekEndDate, String status, Integer achievedWorkoutCount, Integer remainingWorkouts, Boolean isGoalAchieved, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.targetWorkoutCount = targetWorkoutCount;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.status = status;
        this.achievedWorkoutCount = achievedWorkoutCount;
        this.remainingWorkouts = remainingWorkouts;
        this.isGoalAchieved = isGoalAchieved;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(LocalDate weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAchievedWorkoutCount() {
        return achievedWorkoutCount;
    }

    public void setAchievedWorkoutCount(Integer achievedWorkoutCount) {
        this.achievedWorkoutCount = achievedWorkoutCount;
    }

    public Integer getRemainingWorkouts() {
        return remainingWorkouts;
    }

    public void setRemainingWorkouts(Integer remainingWorkouts) {
        this.remainingWorkouts = remainingWorkouts;
    }

    public Boolean getIsGoalAchieved() {
        return isGoalAchieved;
    }

    public void setIsGoalAchieved(Boolean goalAchieved) {
        isGoalAchieved = goalAchieved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
