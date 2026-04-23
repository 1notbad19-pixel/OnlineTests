package com.example.onlinetest.service.impl;

import com.example.onlinetest.dto.UserRequest;
import com.example.onlinetest.dto.UserResponse;
import com.example.onlinetest.mapper.UserMapper;
import com.example.onlinetest.model.User;
import com.example.onlinetest.repository.UserRepository;
import com.example.onlinetest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
  public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        User user = userMapper.toEntity(request);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
  public UserResponse getUser(Long id) {
        return userRepository.findById(id)
        .map(userMapper::toResponse)
        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    @Override
  public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
        .map(userMapper::toResponse)
        .toList();
    }

    @Override
  public UserResponse getUserByUsername(String username) {
        return userRepository.findByUsername(username)
        .map(userMapper::toResponse)
        .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }

    @Override
  @Transactional
  public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        userMapper.update(user, request);

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
  @Transactional
  public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}