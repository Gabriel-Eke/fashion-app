package com.fashion.service;

import com.fashion.model.User;
import com.fashion.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ======================================================
    // ⭐ REGISTER USER
    // ======================================================
    public boolean registerUser(String username, String email, String rawPassword) {

        // Prevent duplicate accounts
        if (userRepository.findByUsername(username) != null ||
            userRepository.findByEmail(email) != null) {
            return false;
        }

        String hash = passwordEncoder.encode(rawPassword);

        // NEW: Default role = USER (important for role-based protection)
        User user = new User(username, email, hash);
        user.setRole("USER");

        userRepository.save(user);
        return true;
    }

    // ======================================================
    // ⭐ LOGIN VALIDATION – Return user ID for session
    // ======================================================
    public Long validateLoginAndReturnId(String username, String rawPassword) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            return null;
        }

        if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            return user.getId();
        }

        return null;
    }

    // ======================================================
    // ⭐ FIND USER BY EMAIL (Password Reset)
    // ======================================================
    public Long findUserIdByEmail(String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return null;
        }

        return user.getId();
    }

    // ======================================================
    // ⭐ UPDATE PASSWORD (Password Reset)
    // ======================================================
    public void updatePassword(Long userId, String newPassword) {

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            String hashed = passwordEncoder.encode(newPassword);
            user.setPasswordHash(hashed);

            userRepository.save(user);
        }
    }

    // ======================================================
    // ⭐ GET USER ROLE (For Admin/User Access Control)
    // ======================================================
    public String getRoleByUserId(Long userId) {

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        // Safety check: if no role assigned, default to USER
        if (user.getRole() == null || user.getRole().isBlank()) {
            return "USER";
        }

        return user.getRole();
    }
}




