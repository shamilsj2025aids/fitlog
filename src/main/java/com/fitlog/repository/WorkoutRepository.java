package com.fitlog.repository;

import com.fitlog.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByUserId(Long userId);

    List<Workout> findByUserIdOrderByWorkoutDateDescLoggedAtDesc(Long userId);

    List<Workout> findByUserIdAndWorkoutDate(Long userId, LocalDate workoutDate);

    List<Workout> findByUserIdAndWorkoutDateBetweenOrderByWorkoutDateAsc(Long userId, LocalDate startDate, LocalDate endDate);

    long countByUserIdAndWorkoutDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    List<Workout> findByUserIdAndWorkoutTypeIgnoreCase(Long userId, String workoutType);

    @Query("SELECT COALESCE(SUM(w.caloriesBurnt), 0.0) FROM Workout w WHERE w.user.id = :userId AND w.workoutDate = :date")
    Double sumCaloriesBurntByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(w.caloriesBurnt), 0.0) FROM Workout w WHERE w.user.id = :userId AND w.workoutDate BETWEEN :startDate AND :endDate")
    Double sumCaloriesBurntByUserIdAndDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(w.durationMinutes), 0) FROM Workout w WHERE w.user.id = :userId AND w.workoutDate = :date")
    Integer sumDurationByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(w.durationMinutes), 0) FROM Workout w WHERE w.user.id = :userId AND w.workoutDate BETWEEN :startDate AND :endDate")
    Integer sumDurationByUserIdAndDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(w.caloriesBurnt), 0.0) FROM Workout w")
    Double sumTotalCaloriesBurnt();
}
