package com.inventra.auth.service;

import com.inventra.auth.dto.LoginRequest;
import com.inventra.auth.entity.User;
import com.inventra.auth.repository.UserRepository;
import com.inventra.auth.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    // ADMIN creates users (EMPLOYEE)
    public User saveUser(User user) {
        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            user.setUserId("ADM-" + System.currentTimeMillis());
        } else {
            user.setUserId("EMP-" + System.currentTimeMillis());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // LOGIN
    public Map<String, String> login(LoginRequest request) {

        Map<String, String> response = new HashMap<>();

        Optional<User> userOptional =
                userRepository.findByEmail(request.getEmail());

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

    public void processForgotPassword(String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return; // silently exit
        }

        User user = userOptional.get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        emailService.sendResetMail(user.getEmail(), token);
    }
    public void resetPassword(String token, String newPassword) {

        if (jwtUtil.isTokenExpired(token)) {
            throw new RuntimeException("Token expired");
        }

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    // Add this method at the end of UserService.java
    public List<String> getAllAdminEmails() {
        return userRepository.findByRole("ADMIN")
                .stream()
                .map(User::getEmail)
                .filter(email -> email != null && !email.isBlank())
                .collect(Collectors.toList());
    }

}
