package com.fitlog.service.impl;

import com.fitlog.dto.GoalRequest;
import com.fitlog.dto.GoalResponse;
import com.fitlog.entity.Goal;
import com.fitlog.entity.User;
import com.fitlog.exception.BusinessRuleViolationException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.GoalRepository;
import com.fitlog.repository.WorkoutRepository;
import com.fitlog.service.GoalService;
import com.fitlog.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final WorkoutRepository workoutRepository;
    private final UserService userService;

    public GoalServiceImpl(GoalRepository goalRepository, WorkoutRepository workoutRepository, UserService userService) {
        this.goalRepository = goalRepository;
        this.workoutRepository = workoutRepository;
        this.userService = userService;
    }

    @Override
    public GoalResponse setWeeklyGoal(GoalRequest request) {
        if (request.getTargetWorkoutCount() == null || request.getTargetWorkoutCount() <= 0) {
            throw new BusinessRuleViolationException("Target workout count must be greater than zero.");
        }

        User user = userService.findUserEntityById(request.getUserId());

        LocalDate weekStart = request.getWeekStartDate() != null
                ? request.getWeekStartDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                : LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        // Check if a goal already exists for this week, update it or create new
        Optional<Goal> existing = goalRepository.findFirstByUserIdAndWeekStartDate(user.getId(), weekStart);
        Goal goal;
        if (existing.isPresent()) {
            goal = existing.get();
            goal.setTargetWorkoutCount(request.getTargetWorkoutCount());
            goal.setWeekEndDate(weekEnd);
        } else {
            goal = new Goal();
            goal.setUser(user);
            goal.setTargetWorkoutCount(request.getTargetWorkoutCount());
            goal.setWeekStartDate(weekStart);
            goal.setWeekEndDate(weekEnd);
            goal.setStatus("ACTIVE");
        }

        Goal saved = goalRepository.save(goal);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public GoalResponse getGoalById(Long id) {
        Goal goal = findGoalEntityById(id);
        return mapToResponse(goal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalResponse> getGoalsByUserId(Long userId) {
        userService.findUserEntityById(userId);
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GoalResponse getActiveGoalForUser(Long userId, LocalDate date) {
        userService.findUserEntityById(userId);
        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDate weekStart = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        return goalRepository.findFirstByUserIdAndWeekStartDate(userId, weekStart)
                .map(this::mapToResponse)
                .orElse(null);
    }

    @Override
    public GoalResponse updateGoal(Long id, GoalRequest request) {
        if (request.getTargetWorkoutCount() == null || request.getTargetWorkoutCount() <= 0) {
            throw new BusinessRuleViolationException("Target workout count must be greater than zero.");
        }

        Goal goal = findGoalEntityById(id);
        goal.setTargetWorkoutCount(request.getTargetWorkoutCount());
        if (request.getWeekStartDate() != null) {
            LocalDate weekStart = request.getWeekStartDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            goal.setWeekStartDate(weekStart);
            goal.setWeekEndDate(weekStart.plusDays(6));
        }

        Goal updated = goalRepository.save(goal);
        return mapToResponse(updated);
    }

    @Override
    public void deleteGoal(Long id) {
        Goal goal = findGoalEntityById(id);
        goalRepository.delete(goal);
    }

    private Goal findGoalEntityById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with ID: " + id));
    }

    private GoalResponse mapToResponse(Goal goal) {
        long completed = workoutRepository.countByUserIdAndWorkoutDateBetween(
                goal.getUser().getId(),
                goal.getWeekStartDate(),
                goal.getWeekEndDate()
        );

        int achieved = (int) completed;
        int target = goal.getTargetWorkoutCount();
        int remaining = Math.max(0, target - achieved);
        boolean isAchieved = achieved >= target;

        String currentStatus = isAchieved ? "ACHIEVED" : goal.getStatus();

        return new GoalResponse(
                goal.getId(),
                goal.getUser().getId(),
                goal.getUser().getName(),
                goal.getTargetWorkoutCount(),
                goal.getWeekStartDate(),
                goal.getWeekEndDate(),
                currentStatus,
                achieved,
                remaining,
                isAchieved,
                goal.getCreatedAt()
        );
    }
}
