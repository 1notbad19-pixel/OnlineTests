package com.example.onlinetest.service;

import com.example.onlinetest.dto.UserRequest;
import com.example.onlinetest.dto.UserResponse;
import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse getUser(Long id);

    List<UserResponse> getAllUsers();

    UserResponse getUserByUsername(String username);

    UserResponse updateUser(Long id, UserRequest request);

    void deleteUser(Long id);
}