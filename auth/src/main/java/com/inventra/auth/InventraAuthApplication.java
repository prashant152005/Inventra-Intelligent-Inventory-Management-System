package com.inventra.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.inventra")
public class InventraAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventraAuthApplication.class, args);
    }
}
