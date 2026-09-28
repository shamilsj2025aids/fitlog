package com.fitlog.service.impl;

import com.fitlog.dto.WorkoutRequest;
import com.fitlog.dto.WorkoutResponse;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.exception.BusinessRuleViolationException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.WorkoutRepository;
import com.fitlog.service.UserService;
import com.fitlog.service.WorkoutService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserService userService;

    public WorkoutServiceImpl(WorkoutRepository workoutRepository, UserService userService) {
        this.workoutRepository = workoutRepository;
        this.userService = userService;
    }

    @Override
    public WorkoutResponse logWorkout(WorkoutRequest request) {
        validateWorkoutBusinessRules(request);

        User user = userService.findUserEntityById(request.getUserId());

        Workout workout = new Workout();
        workout.setUser(user);
        workout.setWorkoutType(request.getWorkoutType().trim());
        workout.setDurationMinutes(request.getDurationMinutes());
        workout.setCaloriesBurnt(request.getCaloriesBurnt());
        workout.setWorkoutDate(request.getWorkoutDate() != null ? request.getWorkoutDate() : LocalDate.now());
        workout.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);

        Workout saved = workoutRepository.save(workout);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutResponse getWorkoutById(Long id) {
        Workout workout = findWorkoutEntityById(id);
        return mapToResponse(workout);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutResponse> getWorkoutsByUserId(Long userId) {
        // Validate user existence
        userService.findUserEntityById(userId);
        return workoutRepository.findByUserIdOrderByWorkoutDateDescLoggedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WorkoutResponse updateWorkout(Long id, WorkoutRequest request) {
        validateWorkoutBusinessRules(request);

        Workout workout = findWorkoutEntityById(id);
        if (!workout.getUser().getId().equals(request.getUserId())) {
            User user = userService.findUserEntityById(request.getUserId());
            workout.setUser(user);
        }

        workout.setWorkoutType(request.getWorkoutType().trim());
        workout.setDurationMinutes(request.getDurationMinutes());
        workout.setCaloriesBurnt(request.getCaloriesBurnt());
        if (request.getWorkoutDate() != null) {
            workout.setWorkoutDate(request.getWorkoutDate());
        }
        workout.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);

        Workout updated = workoutRepository.save(workout);
        return mapToResponse(updated);
    }

    @Override
    public void deleteWorkout(Long id) {
        Workout workout = findWorkoutEntityById(id);
        workoutRepository.delete(workout);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutResponse> filterWorkouts(Long userId, String workoutType, LocalDate startDate, LocalDate endDate) {
        if (userId != null) {
            userService.findUserEntityById(userId);
        }

        List<Workout> workouts;
        if (userId != null && startDate != null && endDate != null) {
            workouts = workoutRepository.findByUserIdAndWorkoutDateBetweenOrderByWorkoutDateAsc(userId, startDate, endDate);
        } else if (userId != null) {
            workouts = workoutRepository.findByUserIdOrderByWorkoutDateDescLoggedAtDesc(userId);
        } else {
            workouts = workoutRepository.findAll();
        }

        return workouts.stream()
                .filter(w -> workoutType == null || workoutType.isBlank() || w.getWorkoutType().equalsIgnoreCase(workoutType.trim()))
                .filter(w -> startDate == null || !w.getWorkoutDate().isBefore(startDate))
                .filter(w -> endDate == null || !w.getWorkoutDate().isAfter(endDate))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateWorkoutBusinessRules(WorkoutRequest request) {
        // Enforce Business Rule: Calories burnt must be a non-negative number
        if (request.getCaloriesBurnt() == null || request.getCaloriesBurnt() < 0) {
            throw new BusinessRuleViolationException("Calories burnt must be a non-negative number.");
        }
        // Enforce Business Rule: Duration must be strictly positive
        if (request.getDurationMinutes() == null || request.getDurationMinutes() <= 0) {
            throw new BusinessRuleViolationException("Workout duration must be greater than zero.");
        }
    }

    private Workout findWorkoutEntityById(Long id) {
        return workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with ID: " + id));
    }

    private WorkoutResponse mapToResponse(Workout workout) {
        return new WorkoutResponse(
                workout.getId(),
                workout.getUser().getId(),
                workout.getUser().getName(),
                workout.getWorkoutType(),
                workout.getDurationMinutes(),
                workout.getCaloriesBurnt(),
                workout.getWorkoutDate(),
                workout.getNotes(),
                workout.getLoggedAt()
        );
    }
}
