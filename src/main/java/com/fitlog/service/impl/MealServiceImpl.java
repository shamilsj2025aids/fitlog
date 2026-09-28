package com.fitlog.service.impl;

import com.fitlog.dto.MealRequest;
import com.fitlog.dto.MealResponse;
import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.exception.BusinessRuleViolationException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.MealRepository;
import com.fitlog.service.MealService;
import com.fitlog.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MealServiceImpl implements MealService {

    private final MealRepository mealRepository;
    private final UserService userService;

    public MealServiceImpl(MealRepository mealRepository, UserService userService) {
        this.mealRepository = mealRepository;
        this.userService = userService;
    }

    @Override
    public MealResponse logMeal(MealRequest request) {
        validateMealBusinessRules(request);

        User user = userService.findUserEntityById(request.getUserId());

        Meal meal = new Meal();
        meal.setUser(user);
        meal.setFoodItem(request.getFoodItem().trim());
        meal.setMealType(request.getMealType().trim().toUpperCase());
        meal.setQuantity(request.getQuantity());
        meal.setCalories(request.getCalories());
        meal.setMealDate(request.getMealDate() != null ? request.getMealDate() : LocalDate.now());

        Meal saved = mealRepository.save(meal);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MealResponse getMealById(Long id) {
        Meal meal = findMealEntityById(id);
        return mapToResponse(meal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealResponse> getMealsByUserId(Long userId) {
        // Validate user exists
        userService.findUserEntityById(userId);
        return mealRepository.findByUserIdOrderByMealDateDescLoggedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MealResponse updateMeal(Long id, MealRequest request) {
        validateMealBusinessRules(request);

        Meal meal = findMealEntityById(id);
        if (!meal.getUser().getId().equals(request.getUserId())) {
            User user = userService.findUserEntityById(request.getUserId());
            meal.setUser(user);
        }

        meal.setFoodItem(request.getFoodItem().trim());
        meal.setMealType(request.getMealType().trim().toUpperCase());
        meal.setQuantity(request.getQuantity());
        meal.setCalories(request.getCalories());
        if (request.getMealDate() != null) {
            meal.setMealDate(request.getMealDate());
        }

        Meal updated = mealRepository.save(meal);
        return mapToResponse(updated);
    }

    @Override
    public void deleteMeal(Long id) {
        Meal meal = findMealEntityById(id);
        mealRepository.delete(meal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealResponse> filterMeals(Long userId, String mealType, LocalDate startDate, LocalDate endDate) {
        if (userId != null) {
            userService.findUserEntityById(userId);
        }

        List<Meal> meals;
        if (userId != null && startDate != null && endDate != null) {
            meals = mealRepository.findByUserIdAndMealDateBetweenOrderByMealDateAsc(userId, startDate, endDate);
        } else if (userId != null) {
            meals = mealRepository.findByUserIdOrderByMealDateDescLoggedAtDesc(userId);
        } else {
            meals = mealRepository.findAll();
        }

        return meals.stream()
                .filter(m -> mealType == null || mealType.isBlank() || m.getMealType().equalsIgnoreCase(mealType.trim()))
                .filter(m -> startDate == null || !m.getMealDate().isBefore(startDate))
                .filter(m -> endDate == null || !m.getMealDate().isAfter(endDate))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateMealBusinessRules(MealRequest request) {
        // Enforce Business Rule: Calories consumed must be a non-negative number
        if (request.getCalories() == null || request.getCalories() < 0) {
            throw new BusinessRuleViolationException("Calories consumed must be a non-negative number.");
        }
    }

    private Meal findMealEntityById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with ID: " + id));
    }

    private MealResponse mapToResponse(Meal meal) {
        return new MealResponse(
                meal.getId(),
                meal.getUser().getId(),
                meal.getUser().getName(),
                meal.getFoodItem(),
                meal.getMealType(),
                meal.getQuantity(),
                meal.getCalories(),
                meal.getMealDate(),
                meal.getLoggedAt()
        );
    }
}
