package com.fitlog.repository;

import com.fitlog.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findByUserId(Long userId);

    List<Meal> findByUserIdOrderByMealDateDescLoggedAtDesc(Long userId);

    List<Meal> findByUserIdAndMealDate(Long userId, LocalDate mealDate);

    List<Meal> findByUserIdAndMealDateBetweenOrderByMealDateAsc(Long userId, LocalDate startDate, LocalDate endDate);

    long countByUserIdAndMealDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    List<Meal> findByUserIdAndMealTypeIgnoreCase(Long userId, String mealType);

    @Query("SELECT COALESCE(SUM(m.calories), 0.0) FROM Meal m WHERE m.user.id = :userId AND m.mealDate = :date")
    Double sumCaloriesByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(m.calories), 0.0) FROM Meal m WHERE m.user.id = :userId AND m.mealDate BETWEEN :startDate AND :endDate")
    Double sumCaloriesByUserIdAndDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(m.calories), 0.0) FROM Meal m")
    Double sumTotalCaloriesConsumed();
}
