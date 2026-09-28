package com.fitlog.service;

import com.fitlog.dto.UserRequest;
import com.fitlog.dto.UserResponse;
import com.fitlog.entity.User;
import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
    User findUserEntityById(Long id);
}
