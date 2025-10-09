package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.enums.UserRole;
import com.ecommerce.ecommerce.repository.UserRepository;
import com.ecommerce.ecommerce.entity.User;
import com.ecommerce.ecommerce.exception.DatabaseOperationException;
import com.ecommerce.ecommerce.exception.InvalidUserDataException;
import com.ecommerce.ecommerce.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

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
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new InvalidUserDataException("Email already in use");
        }
        try {
            user.setUserRole(UserRole.CUSTOMER);
            return userRepository.save(user);
        } catch (DataAccessException ex) {
            log.error("Error saving user", ex);
            throw new DatabaseOperationException("Failed to create user", ex);
        }
    }

    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User existingUser = fetchUserById(id);

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUserRole(UserRole.CUSTOMER);

        try {
            return userRepository.save(existingUser);
        } catch (DataAccessException ex) {
            log.error("Error updating user {}", id, ex);
            throw new DatabaseOperationException("Failed to update user", ex);
        }
    }

    public String deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        try {
            userRepository.deleteById(id);
            return "User deleted successfully (ID: " + id + ")";
        } catch (DataAccessException ex) {
            log.error("Error deleting user {}", id, ex);
            throw new DatabaseOperationException("Failed to delete user", ex);
        }
    }
}
