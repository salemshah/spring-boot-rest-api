package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.service.UserService;
import com.ecommerce.ecommerce.dto.UserRequestDTO;
import com.ecommerce.ecommerce.dto.UserResponseDTO;
import com.ecommerce.ecommerce.entity.User;
import com.ecommerce.ecommerce.mapper.UserMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.fetchAllUsers()
                .stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = userService.fetchUserById(id);
        return ResponseEntity.ok(userMapper.toResponseDTO(user));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        User user = userMapper.toEntity(dto);
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponseDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
                                                      @Valid @RequestBody UserRequestDTO dto) {
        User updatedUser = userMapper.toEntity(dto);
        User result = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(userMapper.toResponseDTO(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        String message = userService.deleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
}
