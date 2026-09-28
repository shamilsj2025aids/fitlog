package com.fitlog.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MealResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String foodItem;
    private String mealType;
    private Double quantity;
    private Double calories;
    private LocalDate mealDate;
    private LocalDateTime loggedAt;

    public MealResponse() {
    }

    public MealResponse(Long id, Long userId, String userName, String foodItem, String mealType, Double quantity, Double calories, LocalDate mealDate, LocalDateTime loggedAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.foodItem = foodItem;
        this.mealType = mealType;
        this.quantity = quantity;
        this.calories = calories;
        this.mealDate = mealDate;
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

    public String getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(String foodItem) {
        this.foodItem = foodItem;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public LocalDate getMealDate() {
        return mealDate;
    }

    public void setMealDate(LocalDate mealDate) {
        this.mealDate = mealDate;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }
}
