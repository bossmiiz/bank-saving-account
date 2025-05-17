package com.banking.savingaccount.service;

import com.banking.savingaccount.dto.RegisterRequest;
import com.banking.savingaccount.model.User;
import com.banking.savingaccount.model.UserRole;

import java.util.Optional;

public interface UserService {
    User register(RegisterRequest request);

    User updateProfile(Long userId, RegisterRequest request);

    User addRole(Long userId, String role);

    User removeRole(Long userId, String role);

    boolean validatePin(Long userId, String pin);

    User registerNewUser(String email, String password, String citizenId,
            String thaiName, String englishName, String pin);

    User createUserByTeller(String citizenId, String thaiName, String englishName);

    Optional<User> findByEmail(String email);

    Optional<User> findByCitizenId(String citizenId);

    boolean existsByEmail(String email);

    boolean existsByCitizenId(String citizenId);

    void addRoleToUser(User user, UserRole role);

    void removeRoleFromUser(User user, UserRole role);

    boolean validatePin(User user, String pin);

    User save(User user);
}