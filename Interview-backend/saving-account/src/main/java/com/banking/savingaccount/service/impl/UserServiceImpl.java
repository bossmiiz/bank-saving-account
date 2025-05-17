package com.banking.savingaccount.service.impl;

import com.banking.savingaccount.dto.RegisterRequest;
import com.banking.savingaccount.model.User;
import com.banking.savingaccount.model.UserRole;
import com.banking.savingaccount.repository.UserRepository;
import com.banking.savingaccount.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByCitizenId(request.getCitizenId())) {
            throw new RuntimeException("Citizen ID already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCitizenId(request.getCitizenId());
        user.setThaiName(request.getThaiName());
        user.setEnglishName(request.getEnglishName());
        user.setPin(request.getPin());
        user.setRoles(new HashSet<>());
        user.getRoles().add(UserRole.ROLE_PERSON);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, RegisterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setCitizenId(request.getCitizenId());
        user.setThaiName(request.getThaiName());
        user.setEnglishName(request.getEnglishName());
        user.setPin(request.getPin());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User addRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getRoles().add(UserRole.valueOf(role));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User removeRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getRoles().remove(UserRole.valueOf(role));
        return userRepository.save(user);
    }

    @Override
    public boolean validatePin(Long userId, String pin) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getPin().equals(pin);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByCitizenId(String citizenId) {
        return userRepository.findByCitizenId(citizenId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByCitizenId(String citizenId) {
        return userRepository.existsByCitizenId(citizenId);
    }

    @Override
    public void addRoleToUser(User user, UserRole role) {
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void removeRoleFromUser(User user, UserRole role) {
        user.getRoles().remove(role);
        userRepository.save(user);
    }

    @Override
    public boolean validatePin(User user, String pin) {
        return user.getPin().equals(pin);
    }

    @Override
    public User registerNewUser(String email, String password, String citizenId,
            String thaiName, String englishName, String pin) {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setCitizenId(citizenId);
        request.setThaiName(thaiName);
        request.setEnglishName(englishName);
        request.setPin(pin);
        return register(request);
    }

    @Override
    @Transactional
    public User createUserByTeller(String citizenId, String thaiName, String englishName) {
        // Check if user exists
        Optional<User> existingUser = userRepository.findByCitizenId(citizenId);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Add ROLE_CUSTOMER if not already has it
            if (!user.getRoles().contains(UserRole.ROLE_CUSTOMER)) {
                user.getRoles().add(UserRole.ROLE_CUSTOMER);
            }
            return userRepository.save(user);
        }

        // Create new user if not exists
        User user = new User();
        user.setCitizenId(citizenId);
        user.setThaiName(thaiName);
        user.setEnglishName(englishName);
        user.setPin("");
        user.setRoles(new HashSet<>());
        user.getRoles().add(UserRole.ROLE_CUSTOMER);

        return userRepository.save(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}