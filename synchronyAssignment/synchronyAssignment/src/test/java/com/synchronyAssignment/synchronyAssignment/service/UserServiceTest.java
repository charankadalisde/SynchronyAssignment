package com.synchronyAssignment.synchronyAssignment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronyAssignment.synchronyAssignment.entity.User;
import com.synchronyAssignment.synchronyAssignment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("john.doe@example.com");
        user.setName("John Doe");
    }

    @Test
    void getUser_whenUserIsInCache_returnsUserFromCache() throws Exception {
        // Arrange
        String userJson = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("user:1")).thenReturn(userJson);
        when(objectMapper.readValue(userJson, User.class)).thenReturn(user);

        // Act
        ResponseEntity<User> response = userService.getUser(1L);


        System.out.printf(response.getBody().toString());
        assertEquals(user.getName(), response.getBody().getName());
        assertEquals(user.getEmail(),response.getBody().getEmail());
    }
}