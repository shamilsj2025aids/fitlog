package com.fitlog.controller;

import com.fitlog.dto.GoalRequest;
import com.fitlog.dto.GoalResponse;
import com.fitlog.service.GoalService;
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
@RequestMapping("/api/goals")
@Tag(name = "Goals", description = "Core Feature 5: Set and track personal weekly workout-count goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    @Operation(summary = "Core Feature 5: Set a personal weekly workout-count goal")
    public ResponseEntity<GoalResponse> setWeeklyGoal(@Valid @RequestBody GoalRequest request) {
        GoalResponse response = goalService.setWeeklyGoal(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal details by ID")
    public ResponseEntity<GoalResponse> getGoalById(@PathVariable Long id) {
        return ResponseEntity.ok(goalService.getGoalById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all goals set by a user")
    public ResponseEntity<List<GoalResponse>> getGoalsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(goalService.getGoalsByUserId(userId));
    }

    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Get currently active weekly goal and completion progress for a user")
    public ResponseEntity<GoalResponse> getActiveGoal(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        GoalResponse response = goalService.getActiveGoalForUser(userId, date);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing weekly goal")
    public ResponseEntity<GoalResponse> updateGoal(@PathVariable Long id, @Valid @RequestBody GoalRequest request) {
        return ResponseEntity.ok(goalService.updateGoal(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a weekly goal by ID")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }
}
