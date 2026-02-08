package com.inventra.auth.repository;

import com.inventra.auth.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Test
    public void saveUser(){
        User user= User.builder()
                .userId("ADM001")
                .username("admin")
                .fullName("System Administrator")
                .email("admin@inventra.com")
                .contactNumber("9876543210")
                .password(passwordEncoder.encode("admin123"))
                .role("ADMIN")
                .isActive(true)
                .build();
        userRepository.save(user);
    }

}