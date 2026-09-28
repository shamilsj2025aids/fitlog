package com.fitlog.service;

import com.fitlog.dto.WorkoutRequest;
import com.fitlog.dto.WorkoutResponse;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.exception.BusinessRuleViolationException;
import com.fitlog.repository.WorkoutRepository;
import com.fitlog.service.impl.WorkoutServiceImpl;
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
class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private WorkoutServiceImpl workoutService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1L, "Alex Johnson", "alex@example.com", 25, 72.0, 178.0, "Male");
    }

    @Test
    @DisplayName("Should successfully log workout when valid inputs provided")
    void logWorkout_Success() {
        WorkoutRequest request = new WorkoutRequest(1L, "Running", 45, 380.0, LocalDate.now(), "Morning run");
        when(userService.findUserEntityById(1L)).thenReturn(sampleUser);

        Workout savedWorkout = new Workout(10L, sampleUser, "Running", 45, 380.0, LocalDate.now(), "Morning run");
        when(workoutRepository.save(any(Workout.class))).thenReturn(savedWorkout);

        WorkoutResponse response = workoutService.logWorkout(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Running", response.getWorkoutType());
        assertEquals(45, response.getDurationMinutes());
        assertEquals(380.0, response.getCaloriesBurnt());
        verify(workoutRepository, times(1)).save(any(Workout.class));
    }

    @Test
    @DisplayName("Business Rule: Should reject negative calories burnt with BusinessRuleViolationException")
    void logWorkout_NegativeCalories_ThrowsException() {
        WorkoutRequest request = new WorkoutRequest(1L, "Running", 45, -50.0, LocalDate.now(), "Invalid");

        BusinessRuleViolationException ex = assertThrows(BusinessRuleViolationException.class, () -> {
            workoutService.logWorkout(request);
        });

        assertEquals("Calories burnt must be a non-negative number.", ex.getMessage());
        verify(workoutRepository, never()).save(any());
    }

    @Test
    @DisplayName("Business Rule: Should reject zero or negative workout duration with BusinessRuleViolationException")
    void logWorkout_InvalidDuration_ThrowsException() {
        WorkoutRequest request = new WorkoutRequest(1L, "Running", 0, 200.0, LocalDate.now(), "Invalid");

        BusinessRuleViolationException ex = assertThrows(BusinessRuleViolationException.class, () -> {
            workoutService.logWorkout(request);
        });

        assertEquals("Workout duration must be greater than zero.", ex.getMessage());
        verify(workoutRepository, never()).save(any());
    }
}
