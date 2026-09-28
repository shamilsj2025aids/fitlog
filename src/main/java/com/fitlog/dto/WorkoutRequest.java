package com.fitlog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class WorkoutRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Workout type is required")
    private String workoutType;

    @NotNull(message = "Duration in minutes is required")
    @Positive(message = "Duration must be greater than zero")
    private Integer durationMinutes;

    @NotNull(message = "Calories burnt is required")
    @Min(value = 0, message = "Calories burnt must be a non-negative number")
    private Double caloriesBurnt;

    private LocalDate workoutDate;

    private String notes;

    public WorkoutRequest() {
    }

    public WorkoutRequest(Long userId, String workoutType, Integer durationMinutes, Double caloriesBurnt, LocalDate workoutDate, String notes) {
        this.userId = userId;
        this.workoutType = workoutType;
        this.durationMinutes = durationMinutes;
        this.caloriesBurnt = caloriesBurnt;
        this.workoutDate = workoutDate;
        this.notes = notes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
