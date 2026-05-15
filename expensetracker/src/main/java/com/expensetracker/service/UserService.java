package com.expensetracker.service;

import com.expensetracker.dto.UserRequest;
import com.expensetracker.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();
}
