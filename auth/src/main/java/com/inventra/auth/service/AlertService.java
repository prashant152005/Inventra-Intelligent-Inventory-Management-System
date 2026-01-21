package com.inventra.auth.service;

import com.inventra.auth.entity.Alert;
import com.inventra.auth.inventory.entity.Product;
import com.inventra.auth.repository.AlertRepository;
import com.inventra.auth.inventory.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

    private final AlertRepository alertRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final UserService userService;

    @Value("${alert.check.interval.minutes:60}")
    private int checkIntervalMinutes;

    public AlertService(AlertRepository alertRepository,
                        ProductRepository productRepository,
                        EmailService emailService,
                        UserService userService) {
        this.alertRepository = alertRepository;
        this.productRepository = productRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    public void createLowStockAlert(Product product) {
        Long productId = product.getProductId();

        boolean hasActiveAlert = !alertRepository.findByProductProductIdAndIsActive(productId, true).isEmpty();

        if (hasActiveAlert) {
            logger.info("Active low-stock alert already exists for product ID: {}", productId);
            return;
        }

        Alert alert = new Alert();
        alert.setProduct(product);
        alert.setAlertType("LOW_STOCK");
        alert.setMessage("Low stock detected: '" + product.getName() +
                "' (Qty: " + product.getQuantity() +
                ", Reorder: " + product.getReorderLevel() + ")");
        alert.setActive(true);
        alert.setTriggeredAt(LocalDateTime.now());

        alertRepository.save(alert);
        logger.info("New low-stock alert created for product ID: {}", productId);

        List<String> adminEmails = userService.getAllAdminEmails();
        emailService.sendLowStockAlert(product, adminEmails);
    }

    /**
     * FIXED: Use valid ISO-8601 duration format PT...M
     * This uses the property value directly as minutes
     */
    @Scheduled(fixedRateString = "PT${alert.check.interval.minutes:60}M")
    public void scheduledLowStockCheck() {
        logger.info("Running scheduled low-stock check...");

        List<Product> lowStockProducts = productRepository.findLowStockProducts();

        for (Product p : lowStockProducts) {
            createLowStockAlert(p);
        }

        logger.info("Scheduled check complete. Found {} low-stock products.", lowStockProducts.size());
    }

    public List<Alert> getActiveLowStockAlerts() {
        return alertRepository.findActiveLowStockAlerts();
    }

    public void resolveAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setActive(false);
        alertRepository.save(alert);
        logger.info("Alert resolved: ID {}", alertId);
    }

    public void resolveAlertsForProduct(Long productId) {
        List<Alert> activeAlerts = alertRepository.findByProductProductIdAndIsActive(productId, true);
        for (Alert alert : activeAlerts) {
            alert.setActive(false);
            alertRepository.save(alert);
        }
        logger.info("Resolved {} active low-stock alert(s) for product ID: {}",
                activeAlerts.size(), productId);
    }

    public void checkAndUpdateAlertForProduct(Product product) {
        if (product == null) return;

        Long productId = product.getProductId();
        boolean isLowStock = product.getQuantity() <= product.getReorderLevel();

        if (isLowStock) {
            List<Alert> activeAlerts = alertRepository.findByProductProductIdAndIsActive(productId, true);
            Alert alert;

            if (activeAlerts.isEmpty()) {
                alert = new Alert();
                alert.setProduct(product);
                alert.setAlertType("LOW_STOCK");
                alert.setTriggeredAt(LocalDateTime.now());
                alert.setActive(true);
            } else {
                alert = activeAlerts.get(0);
            }

            alert.setMessage("Low stock alert: '" + product.getName() +
                    "' (Qty: " + product.getQuantity() +
                    ", Reorder: " + product.getReorderLevel() + ")");
            alertRepository.save(alert);

            logger.info("Low-stock alert {} for product ID: {}",
                    activeAlerts.isEmpty() ? "created" : "updated", productId);

            if (activeAlerts.isEmpty()) {
                List<String> adminEmails = userService.getAllAdminEmails();
                emailService.sendLowStockAlert(product, adminEmails);
            }
        } else {
            resolveAlertsForProduct(productId);
        }
    }
}