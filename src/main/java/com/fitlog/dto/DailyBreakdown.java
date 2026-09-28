package com.fitlog.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyBreakdown {

    private LocalDate date;
    private String dayOfWeek;
    private Integer workoutCount;
    private Integer durationMinutes;
    private Double caloriesBurnt;
    private Double caloriesConsumed;
    private List<String> workoutTypes = new ArrayList<>();

    public DailyBreakdown() {
    }

    public DailyBreakdown(LocalDate date, String dayOfWeek, Integer workoutCount, Integer durationMinutes, Double caloriesBurnt, Double caloriesConsumed, List<String> workoutTypes) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.workoutCount = workoutCount;
        this.durationMinutes = durationMinutes;
        this.caloriesBurnt = caloriesBurnt;
        this.caloriesConsumed = caloriesConsumed;
        this.workoutTypes = workoutTypes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getWorkoutCount() {
        return workoutCount;
    }

    public void setWorkoutCount(Integer workoutCount) {
        this.workoutCount = workoutCount;
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

    public Double getCaloriesConsumed() {
        return caloriesConsumed;
    }

    public void setCaloriesConsumed(Double caloriesConsumed) {
        this.caloriesConsumed = caloriesConsumed;
    }

    public List<String> getWorkoutTypes() {
        return workoutTypes;
    }

    public void setWorkoutTypes(List<String> workoutTypes) {
        this.workoutTypes = workoutTypes;
    }
}
