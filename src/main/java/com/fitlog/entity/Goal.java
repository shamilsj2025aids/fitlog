package com.fitlog.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "workouts", "meals", "goals"})
    private User user;

    @NotNull(message = "Target workout count is required")
    @Positive(message = "Target workout count must be greater than zero")
    @Column(name = "target_workout_count", nullable = false)
    private Integer targetWorkoutCount;

    @NotNull(message = "Week start date is required")
    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @NotNull(message = "Week end date is required")
    @Column(name = "week_end_date", nullable = false)
    private LocalDate weekEndDate;

    @Column(nullable = false, length = 30)
    private String status; // ACTIVE, COMPLETED, IN_PROGRESS

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Goal() {
    }

    public Goal(Long id, User user, Integer targetWorkoutCount, LocalDate weekStartDate, LocalDate weekEndDate, String status) {
        this.id = id;
        this.user = user;
        this.targetWorkoutCount = targetWorkoutCount;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "ACTIVE";
        }
        if (this.weekStartDate != null && this.weekEndDate == null) {
            this.weekEndDate = this.weekStartDate.plusDays(6);
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getTargetWorkoutCount() {
        return targetWorkoutCount;
    }

    public void setTargetWorkoutCount(Integer targetWorkoutCount) {
        this.targetWorkoutCount = targetWorkoutCount;
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
        if (weekStartDate != null && this.weekEndDate == null) {
            this.weekEndDate = weekStartDate.plusDays(6);
        }
    }

    public LocalDate getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(LocalDate weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
