package com.fitlog.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailySummaryResponse {

    private Long userId;
    private String userName;
    private LocalDate date;
    private Double caloriesIn;       // Total calories consumed from meals
    private Double caloriesOut;      // Total calories burnt from workouts
    private Double netCalories;      // caloriesIn - caloriesOut
    private String balanceStatus;    // CALORIE DEFICIT, CALORIE SURPLUS, BALANCED
    private Integer workoutCount;
    private Integer totalWorkoutMinutes;
    private Integer mealCount;
    private List<WorkoutResponse> workouts = new ArrayList<>();
    private List<MealResponse> meals = new ArrayList<>();

    public DailySummaryResponse() {
    }

    public DailySummaryResponse(Long userId, String userName, LocalDate date, Double caloriesIn, Double caloriesOut, Double netCalories, String balanceStatus, Integer workoutCount, Integer totalWorkoutMinutes, Integer mealCount, List<WorkoutResponse> workouts, List<MealResponse> meals) {
        this.userId = userId;
        this.userName = userName;
        this.date = date;
        this.caloriesIn = caloriesIn;
        this.caloriesOut = caloriesOut;
        this.netCalories = netCalories;
        this.balanceStatus = balanceStatus;
        this.workoutCount = workoutCount;
        this.totalWorkoutMinutes = totalWorkoutMinutes;
        this.mealCount = mealCount;
        this.workouts = workouts;
        this.meals = meals;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getCaloriesIn() {
        return caloriesIn;
    }

    public void setCaloriesIn(Double caloriesIn) {
        this.caloriesIn = caloriesIn;
    }

    public Double getCaloriesOut() {
        return caloriesOut;
    }

    public void setCaloriesOut(Double caloriesOut) {
        this.caloriesOut = caloriesOut;
    }

    public Double getNetCalories() {
        return netCalories;
    }

    public void setNetCalories(Double netCalories) {
        this.netCalories = netCalories;
    }

    public String getBalanceStatus() {
        return balanceStatus;
    }

    public void setBalanceStatus(String balanceStatus) {
        this.balanceStatus = balanceStatus;
    }

    public Integer getWorkoutCount() {
        return workoutCount;
    }

    public void setWorkoutCount(Integer workoutCount) {
        this.workoutCount = workoutCount;
    }

    public Integer getTotalWorkoutMinutes() {
        return totalWorkoutMinutes;
    }

    public void setTotalWorkoutMinutes(Integer totalWorkoutMinutes) {
        this.totalWorkoutMinutes = totalWorkoutMinutes;
    }

    public Integer getMealCount() {
        return mealCount;
    }

    public void setMealCount(Integer mealCount) {
        this.mealCount = mealCount;
    }

    public List<WorkoutResponse> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<WorkoutResponse> workouts) {
        this.workouts = workouts;
    }

    public List<MealResponse> getMeals() {
        return meals;
    }

    public void setMeals(List<MealResponse> meals) {
        this.meals = meals;
    }
}
