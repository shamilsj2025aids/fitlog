package com.fitlog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class MealRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Food item is required")
    private String foodItem;

    @NotBlank(message = "Meal type is required")
    private String mealType; // BREAKFAST, LUNCH, DINNER, SNACK

    private Double quantity;

    @NotNull(message = "Calories consumed is required")
    @Min(value = 0, message = "Calories consumed must be a non-negative number")
    private Double calories;

    private LocalDate mealDate;

    public MealRequest() {
    }

    public MealRequest(Long userId, String foodItem, String mealType, Double quantity, Double calories, LocalDate mealDate) {
        this.userId = userId;
        this.foodItem = foodItem;
        this.mealType = mealType;
        this.quantity = quantity;
        this.calories = calories;
        this.mealDate = mealDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
