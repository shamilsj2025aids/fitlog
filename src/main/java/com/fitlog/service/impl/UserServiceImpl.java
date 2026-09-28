package com.fitlog.service.impl;

import com.fitlog.dto.UserRequest;
import com.fitlog.dto.UserResponse;
import com.fitlog.entity.User;
import com.fitlog.exception.DuplicateResourceException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.UserRepository;
import com.fitlog.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("A user with email " + request.getEmail() + " already exists.");
        }
        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setAge(request.getAge());
        user.setWeightKg(request.getWeightKg());
        user.setHeightCm(request.getHeightCm());
        user.setGender(request.getGender());

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUserEntityById(id);
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = findUserEntityById(id);

        // Check if updating email to another existing user's email
        String newEmail = request.getEmail().trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new DuplicateResourceException("A user with email " + newEmail + " already exists.");
        }

        user.setName(request.getName().trim());
        user.setEmail(newEmail);
        user.setAge(request.getAge());
        user.setWeightKg(request.getWeightKg());
        user.setHeightCm(request.getHeightCm());
        user.setGender(request.getGender());

        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    @Override
    public void deleteUser(Long id) {
        User user = findUserEntityById(id);
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getWeightKg(),
                user.getHeightCm(),
                user.getGender(),
                user.getCreatedAt()
        );
    }
}
