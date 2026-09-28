package com.fitlog.service.impl;

import com.fitlog.dto.DashboardOverviewResponse;
import com.fitlog.dto.MealResponse;
import com.fitlog.dto.WorkoutResponse;
import com.fitlog.entity.Meal;
import com.fitlog.entity.Workout;
import com.fitlog.repository.GoalRepository;
import com.fitlog.repository.MealRepository;
import com.fitlog.repository.UserRepository;
import com.fitlog.repository.WorkoutRepository;
import com.fitlog.service.DashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final MealRepository mealRepository;
    private final GoalRepository goalRepository;

    public DashboardServiceImpl(UserRepository userRepository,
                                WorkoutRepository workoutRepository,
                                MealRepository mealRepository,
                                GoalRepository goalRepository) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.mealRepository = mealRepository;
        this.goalRepository = goalRepository;
    }

    @Override
    public DashboardOverviewResponse getOverview() {
        long usersCount = userRepository.count();
        long workoutsCount = workoutRepository.count();
        long mealsCount = mealRepository.count();
        long goalsCount = goalRepository.count();

        Double totalCaloriesBurnt = workoutRepository.sumTotalCaloriesBurnt();
        Double totalCaloriesConsumed = mealRepository.sumTotalCaloriesConsumed();

        int totalMinutes = workoutRepository.findAll().stream()
                .mapToInt(Workout::getDurationMinutes)
                .sum();

        List<WorkoutResponse> recentWorkouts = workoutRepository.findAll(
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "loggedAt"))
        ).getContent().stream()
                .map(w -> new WorkoutResponse(
                        w.getId(),
                        w.getUser().getId(),
                        w.getUser().getName(),
                        w.getWorkoutType(),
                        w.getDurationMinutes(),
                        w.getCaloriesBurnt(),
                        w.getWorkoutDate(),
                        w.getNotes(),
                        w.getLoggedAt()
                ))
                .collect(Collectors.toList());

        List<MealResponse> recentMeals = mealRepository.findAll(
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "loggedAt"))
        ).getContent().stream()
                .map(m -> new MealResponse(
                        m.getId(),
                        m.getUser().getId(),
                        m.getUser().getName(),
                        m.getFoodItem(),
                        m.getMealType(),
                        m.getQuantity(),
                        m.getCalories(),
                        m.getMealDate(),
                        m.getLoggedAt()
                ))
                .collect(Collectors.toList());

        return new DashboardOverviewResponse(
                usersCount,
                workoutsCount,
                mealsCount,
                goalsCount,
                totalCaloriesBurnt != null ? Math.round(totalCaloriesBurnt * 100.0) / 100.0 : 0.0,
                totalCaloriesConsumed != null ? Math.round(totalCaloriesConsumed * 100.0) / 100.0 : 0.0,
                totalMinutes,
                recentWorkouts,
                recentMeals
        );
    }
}
