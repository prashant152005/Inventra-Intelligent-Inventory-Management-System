package com.inventra.auth.controller;

import com.inventra.auth.dto.LoginRequest;
import com.inventra.auth.dto.ResetPasswordRequest;
import com.inventra.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    // ================= FORGOT PASSWORD =================
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");

        userService.processForgotPassword(email);

        return ResponseEntity.ok(
                Map.of("message", "If email exists, reset instructions will be sent")
        );
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        userService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                Map.of("message", "Password reset successful")
        );
    }

}
