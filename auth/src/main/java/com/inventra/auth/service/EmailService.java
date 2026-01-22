package com.inventra.auth.service;

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

import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ================= RESET PASSWORD MAIL =================
    private static final String FRONTEND_URL =
            "https://inventra-frontend.surge.sh";

    public void sendResetMail(String to, String token) {

        try {
            logger.info("Sending password reset email to: {}", to);

            String resetLink =
                    FRONTEND_URL + "/reset-password.html?token=" + token;

            String subject = "Inventra - Password Reset Request";

            String textBody = """
                    Hello,

                    You requested a password reset for your Inventra account.

                    Click the link below to reset your password:
                    %s

                    This link will expire in 24 hours.

                    If you did not request this, please ignore this email.

                    Regards,
                    Inventra Team
                    """.formatted(resetLink);

            sendSimpleEmail(to, subject, textBody);

            logger.info("Password reset email sent successfully to: {}", to);

        } catch (Exception e) {
            logger.error("Failed to send password reset email to {}: {}", to, e.getMessage(), e);
        }
    }

    // ================= LOW STOCK ALERT =================
    public void sendLowStockAlert(Product product, List<String> recipientEmails) {

        if (recipientEmails == null || recipientEmails.isEmpty()) {
            logger.warn("No admin emails found for low stock alert");
            return;
        }

        try {
            logger.info("Sending low stock alert to {} admins for product: {}",
                    recipientEmails.size(), product.getName());

            String subject = "URGENT: Low Stock Alert - " + product.getName();

            String htmlBody = """
                    <html>
                    <body>
                        <h2 style="color: red;">Low Stock Alert!</h2>
                        <p>The following product is low on stock:</p>
                        <ul>
                            <li><strong>Product:</strong> %s</li>
                            <li><strong>SKU:</strong> %s</li>
                            <li><strong>Current Quantity:</strong> %d</li>
                            <li><strong>Reorder Level:</strong> %d</li>
                        </ul>
                        <p>Please restock immediately!</p>
                        <p>Regards,<br/>Inventra Inventory System</p>
                    </body>
                    </html>
                    """.formatted(
                    product.getName(),
                    product.getSku(),
                    product.getQuantity(),
                    product.getReorderLevel()
            );

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipientEmails.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);

            logger.info("Low stock alert sent to {} admins", recipientEmails.size());

        } catch (MessagingException e) {
            logger.error("Failed to send low stock alert: {}", e.getMessage(), e);
        }
    }

    // ================= HELPER =================
    private void sendSimpleEmail(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }


private void sendHtmlEmail(String to, String subject, String htmlContent)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = html

        mailSender.send(message);
    }
}