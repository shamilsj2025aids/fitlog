package com.fitlog.service;

import com.fitlog.dto.WorkoutRequest;
import com.fitlog.dto.WorkoutResponse;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutService {
    WorkoutResponse logWorkout(WorkoutRequest request);
    WorkoutResponse getWorkoutById(Long id);
    List<WorkoutResponse> getWorkoutsByUserId(Long userId);
    WorkoutResponse updateWorkout(Long id, WorkoutRequest request);
    void deleteWorkout(Long id);
    List<WorkoutResponse> filterWorkouts(Long userId, String workoutType, LocalDate startDate, LocalDate endDate);
}
