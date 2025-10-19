package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.dto.LoginRequestDTO;
import com.ecommerce.ecommerce.dto.RegisterRequestDTO;
import com.ecommerce.ecommerce.dto.UserRequestDTO;
import com.ecommerce.ecommerce.dto.UserResponseDTO;
import com.ecommerce.ecommerce.enums.UserRole;
import com.ecommerce.ecommerce.mapper.UserMapper;
import com.ecommerce.ecommerce.repository.UserRepository;
import com.ecommerce.ecommerce.entity.User;
import com.ecommerce.ecommerce.exception.DatabaseOperationException;
import com.ecommerce.ecommerce.exception.InvalidUserDataException;
import com.ecommerce.ecommerce.exception.EntityNotFoundException;
import com.ecommerce.ecommerce.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public List<User> fetchAllUsers() {
        try {
            return userRepository.findAll();
        } catch (DataAccessException ex) {
            log.error("Error fetching users", ex);
            throw new DatabaseOperationException("Failed to fetch users", ex);
        }
    }

    public User fetchUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
    }

    @Transactional
    public User createUserByAdmin(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new InvalidUserDataException("Email already in use");
        }

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (DataAccessException ex) {
            log.error("Error saving user", ex);
            throw new DatabaseOperationException("Failed to create user", ex);
        }
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        String email;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) {
            email = ud.getUsername();
        } else {
            // In your filter you sometimes set principal to the email String // Todo: change the comment
            email = String.valueOf(principal);
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional
    public User registerUser(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        try {
            User user = userMapper.toEntityRegister(request);
            user.setUserRole(UserRole.CUSTOMER);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (DataAccessException ex) {
            log.error("Error saving user", ex);
            throw new DatabaseOperationException("Failed to create user", ex);
        }

    }

    public Map<String, Object> loginAndGenerateTokens(LoginRequestDTO request) {

        User foundUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), foundUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }


        String accessToken = jwtService.generateJwtToken(foundUser);
        String refreshToken = jwtService.generateJwtRefreshToken(foundUser);

        UserResponseDTO userDTO = userMapper.toAuthResponseDTO(foundUser);

        return Map.of(
                "tokenType", "Bearer",
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "user", userDTO
        );
    }

    public Map<String, Object> refreshAccessToken(String refreshToken) {
        // Validate the refresh token
        if (!jwtService.validateJwtRefreshToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        // Extract username (email)
        String email = jwtService.getUserNameFromJwtRefreshToken(refreshToken);

        // Ensure the user still exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String newAccessToken = jwtService.generateJwtToken(user);


        Map<String, Object> data = new LinkedHashMap<>();
        data.put("accessToken", newAccessToken);
        data.put("tokenType", "Bearer");

        return data;
    }

    @Transactional
    public User updateUserByAdmin(Long id, User updatedUser) {
        User existingUser = fetchUserById(id);

        //Check if email is changing
        String newEmail = updatedUser.getEmail();
        if (newEmail != null && !newEmail.equalsIgnoreCase(existingUser.getEmail())) {
            // Check if the new email already belongs to another user
            boolean emailExists = userRepository.existsByEmail(newEmail);
            if (emailExists) {
                throw new InvalidUserDataException("Email already in use");
            }

            existingUser.setEmail(newEmail);
        }

        // Update other fields
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setPhon(updatedUser.getEmail());
        existingUser.setPhon(updatedUser.getPhon());
        existingUser.setUserRole(updatedUser.getUserRole());

        try {
            return userRepository.save(existingUser);
        } catch (DataAccessException ex) {
            log.error("Error updating user {}", id, ex);
            throw new DatabaseOperationException("Failed to update user", ex);
        }
    }

    public String deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User", id);
        }
        try {
            userRepository.deleteById(id);
            return "User deleted successfully (ID: " + id + ")";
        } catch (DataAccessException ex) {
            log.error("Error deleting user {}", id, ex);
            throw new DatabaseOperationException("Failed to delete user", ex);
        }
    }


//    public User getCurrentUser() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String email = auth.getName();
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//    }

    @Transactional
    public User updateCurrentUser(UserRequestDTO dto) {
        User user = getCurrentUser();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhon(dto.getPhon());
        return userRepository.save(user);
    }

    @Transactional
    public String deleteCurrentUser() {
        User user = getCurrentUser();
        userRepository.delete(user);
        return "Account deleted successfully";
    }

}
