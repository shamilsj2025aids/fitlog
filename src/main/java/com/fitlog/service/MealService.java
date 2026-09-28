package com.fitlog.service;

import com.fitlog.dto.MealRequest;
import com.fitlog.dto.MealResponse;

import java.time.LocalDate;
import java.util.List;

public interface MealService {
    MealResponse logMeal(MealRequest request);
    MealResponse getMealById(Long id);
    List<MealResponse> getMealsByUserId(Long userId);
    MealResponse updateMeal(Long id, MealRequest request);
    void deleteMeal(Long id);
    List<MealResponse> filterMeals(Long userId, String mealType, LocalDate startDate, LocalDate endDate);
}
