package com.inventra.auth.service;

import com.inventra.auth.inventory.entity.Batch;
import com.inventra.auth.inventory.entity.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetMail(String to, String token) {
        String resetLink =
                "http://localhost:8080/reset-password.html?token=" + token;

        String subject = "Inventra - Password Reset Request";

        String body = """
                Hello,

                Click the link below to reset your password:
                %s

                Regards,
                Inventra Team
                """.formatted(resetLink);

        sendSimpleEmail(to, subject, body);
    }

    public void sendLowStockAlert(Product product, List<String> recipients) {
        if (recipients == null || recipients.isEmpty()) return;

        String subject = "URGENT: Low Stock Alert - " + product.getName();

        String body =
                "Product: " + product.getName() + "\n" +
                        "SKU: " + product.getSku() + "\n" +
                        "Quantity: " + product.getQuantity() + "\n" +
                        "Reorder Level: " + product.getReorderLevel();

        sendEmailToMany(recipients, subject, body);
    }

    public void sendCombinedAlert(
            List<String> recipients,
            String subject,
            String body
    ) {
        sendEmailToMany(recipients, subject, body);
    }

    private void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    private void sendEmailToMany(List<String> recipients, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(recipients.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
