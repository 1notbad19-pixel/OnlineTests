package com.example.onlinetest.mapper;

import com.example.onlinetest.dto.UserRequest;
import com.example.onlinetest.dto.UserResponse;
import com.example.onlinetest.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        return user;
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getCreatedAt(),
        user.getQuizzes() != null ? user.getQuizzes().size() : 0
    );
    }

    public void update(User user, UserRequest request) {
        if (request.username() != null) {
            user.setUsername(request.username());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.password() != null) {
            user.setPassword(request.password());
        }
        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
    }
}