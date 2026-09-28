package com.fitlog.repository;

import com.fitlog.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserId(Long userId);

    List<Goal> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Goal> findFirstByUserIdAndWeekStartDateLessThanEqualAndWeekEndDateGreaterThanEqualOrderByCreatedAtDesc(
            Long userId, LocalDate date1, LocalDate date2);

    Optional<Goal> findFirstByUserIdAndWeekStartDate(Long userId, LocalDate weekStartDate);

    Optional<Goal> findFirstByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
}
