package com.fitlog.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meals")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "workouts", "meals", "goals"})
    private User user;

    @NotBlank(message = "Food item is required")
    @Column(name = "food_item", nullable = false, length = 150)
    private String foodItem;

    @NotBlank(message = "Meal type is required")
    @Column(name = "meal_type", nullable = false, length = 50)
    private String mealType; // BREAKFAST, LUNCH, DINNER, SNACK

    @Column
    private Double quantity;

    @NotNull(message = "Calories is required")
    @Min(value = 0, message = "Calories consumed must be a non-negative number")
    @Column(nullable = false)
    private Double calories;

    @NotNull(message = "Meal date is required")
    @Column(name = "meal_date", nullable = false)
    private LocalDate mealDate;

    @Column(name = "logged_at", nullable = false, updatable = false)
    private LocalDateTime loggedAt;

    public Meal() {
    }

    public Meal(Long id, User user, String foodItem, String mealType, Double quantity, Double calories, LocalDate mealDate) {
        this.id = id;
        this.user = user;
        this.foodItem = foodItem;
        this.mealType = mealType;
        this.quantity = quantity;
        this.calories = calories;
        this.mealDate = mealDate;
    }

    @PrePersist
    protected void onCreate() {
        if (this.loggedAt == null) {
            this.loggedAt = LocalDateTime.now();
        }
        if (this.mealDate == null) {
            this.mealDate = LocalDate.now();
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
