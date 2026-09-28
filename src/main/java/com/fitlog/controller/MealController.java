package com.fitlog.controller;

import com.fitlog.dto.MealRequest;
import com.fitlog.dto.MealResponse;
import com.fitlog.service.MealService;
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
@RequestMapping("/api/meals")
@Tag(name = "Meals", description = "Core Feature 2: Log meals, manage food entries, and filter nutrition logs")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    @Operation(summary = "Core Feature 2: Log a meal with food item, quantity, and calories")
    public ResponseEntity<MealResponse> logMeal(@Valid @RequestBody MealRequest request) {
        MealResponse response = mealService.logMeal(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get meal details by ID")
    public ResponseEntity<MealResponse> getMealById(@PathVariable Long id) {
        return ResponseEntity.ok(mealService.getMealById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all meals logged by a user")
    public ResponseEntity<List<MealResponse>> getMealsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(mealService.getMealsByUserId(userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing meal log")
    public ResponseEntity<MealResponse> updateMeal(@PathVariable Long id, @Valid @RequestBody MealRequest request) {
        return ResponseEntity.ok(mealService.updateMeal(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a meal log by ID")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        mealService.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    @Operation(summary = "Idea to Go Further: Filter meals by user, meal type, and/or date range")
    public ResponseEntity<List<MealResponse>> filterMeals(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String mealType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(mealService.filterMeals(userId, mealType, startDate, endDate));
    }
}
