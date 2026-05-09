package com.example.onlinetest.service;

import com.example.onlinetest.dto.UserRequest;
import com.example.onlinetest.dto.UserResponse;
import com.example.onlinetest.mapper.UserMapper;
import com.example.onlinetest.model.User;
import com.example.onlinetest.repository.UserRepository;
import com.example.onlinetest.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  private User testUser;
  private UserRequest testRequest;
  private UserResponse testResponse;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("john_doe");
    testUser.setEmail("john@example.com");
    testUser.setFirstName("John");
    testUser.setLastName("Doe");
    testUser.setCreatedAt(LocalDateTime.now());

    testRequest = new UserRequest(
        "john_doe",
        "john@example.com",
        "password123",
        "John",
        "Doe"
    );

    testResponse = new UserResponse(
        1L,
        "john_doe",
        "john@example.com",
        "John",
        "Doe",
        LocalDateTime.now(),
        0
    );
  }

  @Test
  void createUser_Success_ReturnsUserResponse() {
    when(userRepository.existsByUsername("john_doe")).thenReturn(false);
    when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
    when(userMapper.toEntity(testRequest)).thenReturn(testUser);
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    when(userMapper.toResponse(testUser)).thenReturn(testResponse);

    UserResponse result = userService.createUser(testRequest);

    assertNotNull(result);
    assertEquals("john_doe", result.username());
    verify(userRepository).save(testUser);
  }

  @Test
  void createUser_DuplicateUsername_ThrowsException() {
    when(userRepository.existsByUsername("john_doe")).thenReturn(true);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> userService.createUser(testRequest));

    assertEquals("Username already exists: john_doe", exception.getMessage());
    verify(userRepository, never()).save(any());
  }

  @Test
  void createUser_DuplicateEmail_ThrowsException() {
    when(userRepository.existsByUsername("john_doe")).thenReturn(false);
    when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> userService.createUser(testRequest));

    assertEquals("Email already exists: john@example.com", exception.getMessage());
    verify(userRepository, never()).save(any());
  }

  @Test
  void getUser_Success_ReturnsUserResponse() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userMapper.toResponse(testUser)).thenReturn(testResponse);

    UserResponse result = userService.getUser(1L);

    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("john_doe", result.username());
  }

  @Test
  void getUser_NotFound_ThrowsException() {
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> userService.getUser(999L));

    assertEquals("User not found with id: 999", exception.getMessage());
  }

  @Test
  void getUserByUsername_Success_ReturnsUserResponse() {
    when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
    when(userMapper.toResponse(testUser)).thenReturn(testResponse);

    UserResponse result = userService.getUserByUsername("john_doe");

    assertNotNull(result);
    assertEquals("john_doe", result.username());
  }

  @Test
  void getUserByUsername_NotFound_ThrowsException() {
    when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> userService.getUserByUsername("unknown"));

    assertEquals("User not found with username: unknown", exception.getMessage());
  }

  @Test
  void getAllUsers_ReturnsList() {
    when(userRepository.findAll()).thenReturn(List.of(testUser));
    when(userMapper.toResponse(testUser)).thenReturn(testResponse);

    List<UserResponse> result = userService.getAllUsers();

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    assertEquals("john_doe", result.get(0).username());
  }

  @Test
  void getAllUsers_EmptyList_ReturnsEmptyList() {
    when(userRepository.findAll()).thenReturn(List.of());

    List<UserResponse> result = userService.getAllUsers();

    assertTrue(result.isEmpty());
  }

  @Test
  void updateUser_Success_ReturnsUpdatedUser() {
    UserRequest updateRequest = new UserRequest(
        "updated_name", "updated@example.com", "newpass", "Updated", "User"
    );

    User updatedUser = new User();
    updatedUser.setId(1L);
    updatedUser.setUsername("updated_name");
    updatedUser.setEmail("updated@example.com");

    UserResponse updatedResponse = new UserResponse(
        1L, "updated_name", "updated@example.com", "Updated", "User", LocalDateTime.now(), 0
    );

    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);
    when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

    UserResponse result = userService.updateUser(1L, updateRequest);

    assertNotNull(result);
    assertEquals("updated_name", result.username());
    verify(userMapper).update(testUser, updateRequest);
  }

  @Test
  void updateUser_NotFound_ThrowsException() {
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> userService.updateUser(999L, testRequest));

    assertEquals("User not found with id: 999", exception.getMessage());
    verify(userRepository, never()).save(any());
  }

  @Test
  void deleteUser_Success_DeletesUser() {
    when(userRepository.existsById(1L)).thenReturn(true);
    doNothing().when(userRepository).deleteById(1L);

    assertDoesNotThrow(() -> userService.deleteUser(1L));
    verify(userRepository).deleteById(1L);
  }

  @Test
  void deleteUser_NotFound_ThrowsException() {
    when(userRepository.existsById(999L)).thenReturn(false);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> userService.deleteUser(999L));

    assertEquals("User not found with id: 999", exception.getMessage());
    verify(userRepository, never()).deleteById(any());
  }
}