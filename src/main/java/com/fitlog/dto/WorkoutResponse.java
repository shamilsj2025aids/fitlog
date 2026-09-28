package com.fitlog.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WorkoutResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String workoutType;
    private Integer durationMinutes;
    private Double caloriesBurnt;
    private LocalDate workoutDate;
    private String notes;
    private LocalDateTime loggedAt;

    public WorkoutResponse() {
    }

    public WorkoutResponse(Long id, Long userId, String userName, String workoutType, Integer durationMinutes, Double caloriesBurnt, LocalDate workoutDate, String notes, LocalDateTime loggedAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.workoutType = workoutType;
        this.durationMinutes = durationMinutes;
        this.caloriesBurnt = caloriesBurnt;
        this.workoutDate = workoutDate;
        this.notes = notes;
        this.loggedAt = loggedAt;
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

    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getCaloriesBurnt() {
        return caloriesBurnt;
    }

    public void setCaloriesBurnt(Double caloriesBurnt) {
        this.caloriesBurnt = caloriesBurnt;
    }

    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }
}
