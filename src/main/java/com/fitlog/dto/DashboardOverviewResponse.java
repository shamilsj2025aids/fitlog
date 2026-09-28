package com.fitlog.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardOverviewResponse {

    private Long totalUsersCount;
    private Long totalWorkoutsLogged;
    private Long totalMealsLogged;
    private Long totalGoalsSet;
    private Double totalCaloriesBurntSystemWide;
    private Double totalCaloriesConsumedSystemWide;
    private Integer totalWorkoutMinutesSystemWide;
    private List<WorkoutResponse> recentWorkouts = new ArrayList<>();
    private List<MealResponse> recentMeals = new ArrayList<>();

    public DashboardOverviewResponse() {
    }

    public DashboardOverviewResponse(Long totalUsersCount, Long totalWorkoutsLogged, Long totalMealsLogged, Long totalGoalsSet, Double totalCaloriesBurntSystemWide, Double totalCaloriesConsumedSystemWide, Integer totalWorkoutMinutesSystemWide, List<WorkoutResponse> recentWorkouts, List<MealResponse> recentMeals) {
        this.totalUsersCount = totalUsersCount;
        this.totalWorkoutsLogged = totalWorkoutsLogged;
        this.totalMealsLogged = totalMealsLogged;
        this.totalGoalsSet = totalGoalsSet;
        this.totalCaloriesBurntSystemWide = totalCaloriesBurntSystemWide;
        this.totalCaloriesConsumedSystemWide = totalCaloriesConsumedSystemWide;
        this.totalWorkoutMinutesSystemWide = totalWorkoutMinutesSystemWide;
        this.recentWorkouts = recentWorkouts;
        this.recentMeals = recentMeals;
    }

    public Long getTotalUsersCount() {
        return totalUsersCount;
    }

    public void setTotalUsersCount(Long totalUsersCount) {
        this.totalUsersCount = totalUsersCount;
    }

    public Long getTotalWorkoutsLogged() {
        return totalWorkoutsLogged;
    }

    public void setTotalWorkoutsLogged(Long totalWorkoutsLogged) {
        this.totalWorkoutsLogged = totalWorkoutsLogged;
    }

    public Long getTotalMealsLogged() {
        return totalMealsLogged;
    }

    public void setTotalMealsLogged(Long totalMealsLogged) {
        this.totalMealsLogged = totalMealsLogged;
    }

    public Long getTotalGoalsSet() {
        return totalGoalsSet;
    }

    public void setTotalGoalsSet(Long totalGoalsSet) {
        this.totalGoalsSet = totalGoalsSet;
    }

    public Double getTotalCaloriesBurntSystemWide() {
        return totalCaloriesBurntSystemWide;
    }

    public void setTotalCaloriesBurntSystemWide(Double totalCaloriesBurntSystemWide) {
        this.totalCaloriesBurntSystemWide = totalCaloriesBurntSystemWide;
    }

    public Double getTotalCaloriesConsumedSystemWide() {
        return totalCaloriesConsumedSystemWide;
    }

    public void setTotalCaloriesConsumedSystemWide(Double totalCaloriesConsumedSystemWide) {
        this.totalCaloriesConsumedSystemWide = totalCaloriesConsumedSystemWide;
    }

    public Integer getTotalWorkoutMinutesSystemWide() {
        return totalWorkoutMinutesSystemWide;
    }

    public void setTotalWorkoutMinutesSystemWide(Integer totalWorkoutMinutesSystemWide) {
        this.totalWorkoutMinutesSystemWide = totalWorkoutMinutesSystemWide;
    }

    public List<WorkoutResponse> getRecentWorkouts() {
        return recentWorkouts;
    }

    public void setRecentWorkouts(List<WorkoutResponse> recentWorkouts) {
        this.recentWorkouts = recentWorkouts;
    }

    public List<MealResponse> getRecentMeals() {
        return recentMeals;
    }

    public void setRecentMeals(List<MealResponse> recentMeals) {
        this.recentMeals = recentMeals;
    }
}
