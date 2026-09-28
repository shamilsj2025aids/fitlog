package com.fitlog.service.impl;

import com.fitlog.dto.*;
import com.fitlog.entity.Goal;
import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.exception.BusinessRuleViolationException;
import com.fitlog.repository.GoalRepository;
import com.fitlog.repository.MealRepository;
import com.fitlog.repository.WorkoutRepository;
import com.fitlog.service.GoalService;
import com.fitlog.service.SummaryService;
import com.fitlog.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SummaryServiceImpl implements SummaryService {

    private final WorkoutRepository workoutRepository;
    private final MealRepository mealRepository;
    private final GoalRepository goalRepository;
    private final UserService userService;
    private final GoalService goalService;

    public SummaryServiceImpl(WorkoutRepository workoutRepository,
                              MealRepository mealRepository,
                              GoalRepository goalRepository,
                              UserService userService,
                              GoalService goalService) {
        this.workoutRepository = workoutRepository;
        this.mealRepository = mealRepository;
        this.goalRepository = goalRepository;
        this.userService = userService;
        this.goalService = goalService;
    }

    @Override
    public DailySummaryResponse getDailySummary(Long userId, LocalDate date) {
        User user = userService.findUserEntityById(userId);
        LocalDate targetDate = date != null ? date : LocalDate.now();

        List<Workout> workouts = workoutRepository.findByUserIdAndWorkoutDate(userId, targetDate);
        List<Meal> meals = mealRepository.findByUserIdAndMealDate(userId, targetDate);

        double caloriesOut = workouts.stream().mapToDouble(Workout::getCaloriesBurnt).sum();
        double caloriesIn = meals.stream().mapToDouble(Meal::getCalories).sum();
        double netCalories = caloriesIn - caloriesOut;

        int totalWorkoutMinutes = workouts.stream().mapToInt(Workout::getDurationMinutes).sum();

        String balanceStatus;
        if (netCalories > 0) {
            balanceStatus = "CALORIE SURPLUS";
        } else if (netCalories < 0) {
            balanceStatus = "CALORIE DEFICIT";
        } else {
            balanceStatus = "BALANCED";
        }

        List<WorkoutResponse> workoutResponses = workouts.stream()
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

        List<MealResponse> mealResponses = meals.stream()
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

        return new DailySummaryResponse(
                user.getId(),
                user.getName(),
                targetDate,
                round(caloriesIn),
                round(caloriesOut),
                round(netCalories),
                balanceStatus,
                workouts.size(),
                totalWorkoutMinutes,
                meals.size(),
                workoutResponses,
                mealResponses
        );
    }

    @Override
    public WeeklyTrendResponse getWeeklyTrend(Long userId, LocalDate weekStartDate) {
        User user = userService.findUserEntityById(userId);

        LocalDate monday = weekStartDate != null
                ? weekStartDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                : LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);

        long workoutEntriesCount = workoutRepository.countByUserIdAndWorkoutDateBetween(userId, monday, sunday);
        long mealEntriesCount = mealRepository.countByUserIdAndMealDateBetween(userId, monday, sunday);

        // ENFORCE BUSINESS RULE: "A weekly summary is only generated once at least one entry exists for that week."
        if (workoutEntriesCount == 0 && mealEntriesCount == 0) {
            throw new BusinessRuleViolationException(
                    "No workout or meal entries exist for the week of " + monday + " to " + sunday +
                    ". A weekly summary is only generated once at least one entry exists for that week."
            );
        }

        List<Workout> weeklyWorkouts = workoutRepository.findByUserIdAndWorkoutDateBetweenOrderByWorkoutDateAsc(userId, monday, sunday);
        List<Meal> weeklyMeals = mealRepository.findByUserIdAndMealDateBetweenOrderByMealDateAsc(userId, monday, sunday);

        List<DailyBreakdown> breakdowns = new ArrayList<>();
        double totalCaloriesBurnt = 0.0;
        double totalCaloriesConsumed = 0.0;
        int totalDuration = 0;
        int totalWorkouts = weeklyWorkouts.size();

        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = monday.plusDays(i);
            String dayName = currentDay.getDayOfWeek().name();

            List<Workout> dayWorkouts = weeklyWorkouts.stream()
                    .filter(w -> w.getWorkoutDate().equals(currentDay))
                    .collect(Collectors.toList());

            List<Meal> dayMeals = weeklyMeals.stream()
                    .filter(m -> m.getMealDate().equals(currentDay))
                    .collect(Collectors.toList());

            double dayBurnt = dayWorkouts.stream().mapToDouble(Workout::getCaloriesBurnt).sum();
            double dayConsumed = dayMeals.stream().mapToDouble(Meal::getCalories).sum();
            int dayDuration = dayWorkouts.stream().mapToInt(Workout::getDurationMinutes).sum();
            List<String> types = dayWorkouts.stream().map(Workout::getWorkoutType).distinct().collect(Collectors.toList());

            breakdowns.add(new DailyBreakdown(
                    currentDay,
                    dayName,
                    dayWorkouts.size(),
                    dayDuration,
                    round(dayBurnt),
                    round(dayConsumed),
                    types
            ));

            totalCaloriesBurnt += dayBurnt;
            totalCaloriesConsumed += dayConsumed;
            totalDuration += dayDuration;
        }

        double netWeeklyCalories = totalCaloriesConsumed - totalCaloriesBurnt;

        // Fetch Goal for this week if set
        Optional<Goal> goalOpt = goalRepository.findFirstByUserIdAndWeekStartDate(userId, monday);
        GoalResponse goalSummary = goalOpt.map(goal -> goalService.getGoalById(goal.getId())).orElse(null);

        return new WeeklyTrendResponse(
                user.getId(),
                user.getName(),
                monday,
                sunday,
                totalWorkouts,
                totalDuration,
                round(totalCaloriesBurnt),
                round(totalCaloriesConsumed),
                round(netWeeklyCalories),
                breakdowns,
                goalSummary
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
