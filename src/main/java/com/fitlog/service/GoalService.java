package com.fitlog.service;

import com.fitlog.dto.GoalRequest;
import com.fitlog.dto.GoalResponse;

import java.time.LocalDate;
import java.util.List;

public interface GoalService {
    GoalResponse setWeeklyGoal(GoalRequest request);
    GoalResponse getGoalById(Long id);
    List<GoalResponse> getGoalsByUserId(Long userId);
    GoalResponse getActiveGoalForUser(Long userId, LocalDate date);
    GoalResponse updateGoal(Long id, GoalRequest request);
    void deleteGoal(Long id);
}
