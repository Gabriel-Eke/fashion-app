package com.fashion.web;

import com.fashion.model.PasswordResetToken;
import com.fashion.service.UserService;
import com.fashion.repository.PasswordResetTokenRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class AuthController {

    private final UserService userService;
    private final PasswordResetTokenRepository tokenRepository;

    public AuthController(UserService userService,
                          PasswordResetTokenRepository tokenRepository) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
    }

    // ===========================
    // ⭐ LOGIN PAGE
    // ===========================
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              Model model,
                              HttpSession session) {

        Long userId = userService.validateLoginAndReturnId(username, password);

        if (userId == null) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }

        // Store user ID in session
        session.setAttribute("userId", userId);

        // Load user role for role-based access
        String role = userService.getRoleByUserId(userId);
        session.setAttribute("role", role);

        return "redirect:/customer-transactions";
    }

    // ===========================
    // ⭐ REGISTER PAGE
    // ===========================
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String username,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 Model model) {

        boolean created = userService.registerUser(username, email, password);

        if (!created) {
            model.addAttribute("error", "Username or email already in use.");
            return "register";
        }

        model.addAttribute("message", "Account created! Please log in.");
        return "login";
    }

    // ===========================
    // ⭐ FORGOT PASSWORD PAGE
    // ===========================
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email, Model model) {

        Long userId = userService.findUserIdByEmail(email);

        if (userId != null) {
            // Create token
            PasswordResetToken token = new PasswordResetToken();
            token.setUserId(userId);
            token.setToken(UUID.randomUUID().toString());
            token.setExpiryDate(LocalDateTime.now().plusHours(1));
            token.setUsed(false);

            tokenRepository.save(token);

            String resetLink = "http://localhost:8080/reset-password?token=" + token.getToken();

            // TODO: Replace this with an email sender
            System.out.println("Password Reset Link: " + resetLink);
        }

        model.addAttribute("message",
                "If an account with this email exists, a reset link has been sent.");
        return "forgot-password";
    }

    // ===========================
    // ⭐ RESET PASSWORD PAGE
    // ===========================
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {

        PasswordResetToken t = tokenRepository.findByToken(token)
                .orElse(null);

        if (t == null || t.isUsed() || t.getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Invalid or expired password reset link.");
            return "reset-password-error";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    // ===========================
    // ⭐ HANDLE RESET PASSWORD
    // ===========================
    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String token,
                                      @RequestParam String password,
                                      Model model) {

        PasswordResetToken t = tokenRepository.findByToken(token)
                .orElse(null);

        if (t == null || t.isUsed() || t.getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Invalid or expired token.");
            return "reset-password-error";
        }

        userService.updatePassword(t.getUserId(), password);

        t.setUsed(true);
        tokenRepository.save(t);

        model.addAttribute("message", "Password updated successfully. Please log in.");
        return "login";
    }

    // ===========================
    // ⭐ LOGOUT
    // ===========================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}






