package com.inventra.auth.service;

import com.inventra.auth.dto.LoginRequest;
import com.inventra.auth.entity.User;
import com.inventra.auth.repository.UserRepository;
import com.inventra.auth.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    // ================= ADMIN CREATE USER =================
    public User saveUser(User user) {
        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            user.setUserId("ADM-" + System.currentTimeMillis());
        } else {
            user.setUserId("EMP-" + System.currentTimeMillis());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // ================= LOGIN =================
    public Map<String, String> login(LoginRequest request) {

        Map<String, String> response = new HashMap<>();

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            response.put("error", "User not found");
            return response;
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.put("error", "Invalid password");
            return response;
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        response.put("token", token);
        response.put("role", user.getRole());

        return response;
    }

    // ================= FORGOT PASSWORD =================
    public void processForgotPassword(String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) return;

        User user = userOptional.get();

        String resetToken = UUID.randomUUID().toString();

        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        emailService.sendResetMail(user.getEmail(), resetToken);
    }

    // ================= RESET PASSWORD =================
    public void resetPassword(String token, String newPassword) {

        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }

    // ================= ADMIN EMAILS =================
    public List<String> getAllAdminEmails() {
        return userRepository.findByRole("ADMIN")
                .stream()
                .map(User::getEmail)
                .filter(email -> email != null && !email.isBlank())
                .collect(Collectors.toList());
    }
}
