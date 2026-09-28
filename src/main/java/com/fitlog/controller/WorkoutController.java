package com.fitlog.controller;

import com.fitlog.dto.WorkoutRequest;
import com.fitlog.dto.WorkoutResponse;
import com.fitlog.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@Tag(name = "Workouts", description = "Core Feature 1: Log workouts, manage workout records, and filter activities")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    @Operation(summary = "Core Feature 1: Log a workout with type, duration, and estimated calories burnt")
    public ResponseEntity<WorkoutResponse> logWorkout(@Valid @RequestBody WorkoutRequest request) {
        WorkoutResponse response = workoutService.logWorkout(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workout details by ID")
    public ResponseEntity<WorkoutResponse> getWorkoutById(@PathVariable Long id) {
        return ResponseEntity.ok(workoutService.getWorkoutById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all workouts logged by a user")
    public ResponseEntity<List<WorkoutResponse>> getWorkoutsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(workoutService.getWorkoutsByUserId(userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing workout log")
    public ResponseEntity<WorkoutResponse> updateWorkout(@PathVariable Long id, @Valid @RequestBody WorkoutRequest request) {
        return ResponseEntity.ok(workoutService.updateWorkout(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a workout log by ID")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    @Operation(summary = "Idea to Go Further: Filter workouts by user, type, and/or date range")
    public ResponseEntity<List<WorkoutResponse>> filterWorkouts(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(workoutService.filterWorkouts(userId, type, startDate, endDate));
    }
}
