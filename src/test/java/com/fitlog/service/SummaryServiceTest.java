package com.fitlog.service;

import com.fitlog.dto.DailySummaryResponse;
import com.fitlog.dto.WeeklyTrendResponse;
import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.exception.BusinessRuleViolationException;
import com.fitlog.repository.GoalRepository;
import com.fitlog.repository.MealRepository;
import com.fitlog.repository.WorkoutRepository;
import com.fitlog.service.impl.SummaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserService userService;

    @Mock
    private GoalService goalService;

    @InjectMocks
    private SummaryServiceImpl summaryService;

    private User sampleUser;
    private LocalDate monday;
    private LocalDate sunday;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1L, "Alex Johnson", "alex@example.com", 25, 72.0, 178.0, "Male");
        monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        sunday = monday.plusDays(6);
    }

    @Test
    @DisplayName("Business Rule: Weekly summary is rejected if zero workout and meal entries exist for that week")
    void getWeeklyTrend_NoEntries_ThrowsBusinessRuleException() {
        when(userService.findUserEntityById(1L)).thenReturn(sampleUser);
        when(workoutRepository.countByUserIdAndWorkoutDateBetween(1L, monday, sunday)).thenReturn(0L);
        when(mealRepository.countByUserIdAndMealDateBetween(1L, monday, sunday)).thenReturn(0L);

        BusinessRuleViolationException ex = assertThrows(BusinessRuleViolationException.class, () -> {
            summaryService.getWeeklyTrend(1L, monday);
        });

        assertTrue(ex.getMessage().contains("A weekly summary is only generated once at least one entry exists for that week"));
    }

    @Test
    @DisplayName("Should successfully calculate weekly trend when entries exist")
    void getWeeklyTrend_WithEntries_Success() {
        when(userService.findUserEntityById(1L)).thenReturn(sampleUser);
        when(workoutRepository.countByUserIdAndWorkoutDateBetween(1L, monday, sunday)).thenReturn(1L);
        when(mealRepository.countByUserIdAndMealDateBetween(1L, monday, sunday)).thenReturn(1L);

        Workout w = new Workout(1L, sampleUser, "Running", 30, 250.0, monday, "Morning run");
        Meal m = new Meal(1L, sampleUser, "Salad", "LUNCH", 200.0, 350.0, monday);

        when(workoutRepository.findByUserIdAndWorkoutDateBetweenOrderByWorkoutDateAsc(1L, monday, sunday))
                .thenReturn(List.of(w));
        when(mealRepository.findByUserIdAndMealDateBetweenOrderByMealDateAsc(1L, monday, sunday))
                .thenReturn(List.of(m));
        when(goalRepository.findFirstByUserIdAndWeekStartDate(1L, monday)).thenReturn(Optional.empty());

        WeeklyTrendResponse response = summaryService.getWeeklyTrend(1L, monday);

        assertNotNull(response);
        assertEquals(1, response.getTotalWorkoutsCompleted());
        assertEquals(30, response.getTotalDurationMinutes());
        assertEquals(250.0, response.getTotalCaloriesBurnt());
        assertEquals(350.0, response.getTotalCaloriesConsumed());
        assertEquals(100.0, response.getNetWeeklyCalories());
        assertEquals(7, response.getDailyBreakdowns().size());
    }

    @Test
    @DisplayName("Should correctly calculate daily summary of calories in vs calories out")
    void getDailySummary_Success() {
        LocalDate today = LocalDate.now();
        when(userService.findUserEntityById(1L)).thenReturn(sampleUser);

        Workout w = new Workout(1L, sampleUser, "Cycling", 60, 500.0, today, "Evening cycle");
        Meal m1 = new Meal(1L, sampleUser, "Eggs & Toast", "BREAKFAST", 150.0, 400.0, today);
        Meal m2 = new Meal(2L, sampleUser, "Grilled Chicken Rice", "LUNCH", 300.0, 650.0, today);

        when(workoutRepository.findByUserIdAndWorkoutDate(1L, today)).thenReturn(List.of(w));
        when(mealRepository.findByUserIdAndMealDate(1L, today)).thenReturn(List.of(m1, m2));

        DailySummaryResponse response = summaryService.getDailySummary(1L, today);

        assertNotNull(response);
        assertEquals(1050.0, response.getCaloriesIn());  // 400 + 650
        assertEquals(500.0, response.getCaloriesOut());  // 500
        assertEquals(550.0, response.getNetCalories());   // 1050 - 500 = +550
        assertEquals("CALORIE SURPLUS", response.getBalanceStatus());
        assertEquals(1, response.getWorkoutCount());
        assertEquals(2, response.getMealCount());
    }
}
