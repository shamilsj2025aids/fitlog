package com.fitlog.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "workouts", "meals", "goals"})
    private User user;

    @NotBlank(message = "Workout type is required")
    @Column(name = "workout_type", nullable = false, length = 100)
    private String workoutType;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be greater than zero")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @NotNull(message = "Calories burnt is required")
    @Min(value = 0, message = "Calories burnt must be a non-negative number")
    @Column(name = "calories_burnt", nullable = false)
    private Double caloriesBurnt;

    @NotNull(message = "Workout date is required")
    @Column(name = "workout_date", nullable = false)
    private LocalDate workoutDate;

    @Column(length = 500)
    private String notes;

    @Column(name = "logged_at", nullable = false, updatable = false)
    private LocalDateTime loggedAt;

    public Workout() {
    }

    public Workout(Long id, User user, String workoutType, Integer durationMinutes, Double caloriesBurnt, LocalDate workoutDate, String notes) {
        this.id = id;
        this.user = user;
        this.workoutType = workoutType;
        this.durationMinutes = durationMinutes;
        this.caloriesBurnt = caloriesBurnt;
        this.workoutDate = workoutDate;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        if (this.loggedAt == null) {
            this.loggedAt = LocalDateTime.now();
        }
        if (this.workoutDate == null) {
            this.workoutDate = LocalDate.now();
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

    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getCaloriesBurnt() {
        return caloriesBurnt;
    }

    public void setCaloriesBurnt(Double caloriesBurnt) {
        this.caloriesBurnt = caloriesBurnt;
    }

    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }
}
