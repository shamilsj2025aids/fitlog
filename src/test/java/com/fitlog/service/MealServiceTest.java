package com.fitlog.service;

import com.fitlog.dto.MealRequest;
import com.fitlog.dto.MealResponse;
import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.exception.BusinessRuleViolationException;
import com.fitlog.repository.MealRepository;
import com.fitlog.service.impl.MealServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private MealServiceImpl mealService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1L, "Alex Johnson", "alex@example.com", 25, 72.0, 178.0, "Male");
    }

    @Test
    @DisplayName("Should successfully log meal when valid inputs provided")
    void logMeal_Success() {
        MealRequest request = new MealRequest(1L, "Oatmeal with berries", "BREAKFAST", 150.0, 320.0, LocalDate.now());
        when(userService.findUserEntityById(1L)).thenReturn(sampleUser);

        Meal savedMeal = new Meal(20L, sampleUser, "Oatmeal with berries", "BREAKFAST", 150.0, 320.0, LocalDate.now());
        when(mealRepository.save(any(Meal.class))).thenReturn(savedMeal);

        MealResponse response = mealService.logMeal(request);

        assertNotNull(response);
        assertEquals(20L, response.getId());
        assertEquals("Oatmeal with berries", response.getFoodItem());
        assertEquals("BREAKFAST", response.getMealType());
        assertEquals(320.0, response.getCalories());
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    @DisplayName("Business Rule: Should reject negative calories consumed with BusinessRuleViolationException")
    void logMeal_NegativeCalories_ThrowsException() {
        MealRequest request = new MealRequest(1L, "Apple", "SNACK", 100.0, -95.0, LocalDate.now());

        BusinessRuleViolationException ex = assertThrows(BusinessRuleViolationException.class, () -> {
            mealService.logMeal(request);
        });

        assertEquals("Calories consumed must be a non-negative number.", ex.getMessage());
        verify(mealRepository, never()).save(any());
    }
}
