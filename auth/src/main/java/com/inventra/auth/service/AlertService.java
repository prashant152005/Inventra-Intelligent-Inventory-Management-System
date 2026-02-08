package com.inventra.auth.service;

import com.inventra.auth.entity.Alert;
import com.inventra.auth.inventory.entity.Batch;
import com.inventra.auth.inventory.entity.Product;
import com.inventra.auth.inventory.repository.BatchRepository;
import com.inventra.auth.inventory.repository.ProductRepository;
import com.inventra.auth.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final BatchRepository batchRepository;
    private final EmailService emailService;
    private final UserService userService;

    public AlertService(
            AlertRepository alertRepository,
            BatchRepository batchRepository,
            EmailService emailService,
            UserService userService
    ) {
        this.alertRepository = alertRepository;
        this.batchRepository = batchRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    public void checkCombinedLowStockAndExpiry(Product product) {

        // 1️⃣ Low stock check
        if (product.getQuantity() > product.getReorderLevel()) return;

        // 2️⃣ Near-expiry batches (next 3 days)
        List<Batch> nearExpiryBatches =
                batchRepository.findByProductProductIdAndExpiryDateBetween(
                        product.getProductId(),
                        LocalDate.now(),
                        LocalDate.now().plusDays(3)
                );

        if (nearExpiryBatches.isEmpty()) return;

        // 3️⃣ Prevent duplicate alerts
        boolean exists =
                alertRepository.existsByProductProductIdAndAlertTypeAndIsActive(
                        product.getProductId(),
                        "LOW_STOCK_NEAR_EXPIRY",
                        true
                );

        if (exists) return;

        // 4️⃣ Pick earliest batch
        Batch batch = nearExpiryBatches.get(0);

        long daysLeft = ChronoUnit.DAYS.between(
                LocalDate.now(),
                batch.getExpiryDate()
        );

        // 5️⃣ Save alert
        Alert alert = new Alert();
        alert.setProduct(product);
        alert.setAlertType("LOW_STOCK_NEAR_EXPIRY");
        alert.setMessage(
                product.getName() +
                        " is low stock and expires in " +
                        daysLeft + " day(s)"
        );
        alert.setActive(true);
        alert.setTriggeredAt(LocalDateTime.now());

        alertRepository.save(alert);

        // 6️⃣ Email admins
        List<String> admins = userService.getAllAdminEmails();

        String subject = "⚠️ Critical Inventory Alert";
        String body =
                product.getName() + " is LOW STOCK and NEAR EXPIRY\n\n" +
                        "Quantity: " + product.getQuantity() + "\n" +
                        "Reorder Level: " + product.getReorderLevel() + "\n" +
                        "Batch: " + batch.getBatchNumber() + "\n" +
                        "Expires in: " + daysLeft + " day(s)";

        emailService.sendCombinedAlert(admins, subject, body);
    }

    public List<Alert> getActiveLowStockAlerts() {
        return alertRepository.findByAlertTypeAndIsActive(
                "LOW_STOCK",
                true
        );
    }
    public void checkLowStock(Product product) {

        // 1️⃣ Check condition
        if (product.getQuantity() > product.getReorderLevel()) return;

        // 2️⃣ Prevent duplicate LOW_STOCK alert
        boolean exists = alertRepository
                .existsByProductProductIdAndAlertTypeAndIsActive(
                        product.getProductId(),
                        "LOW_STOCK",
                        true
                );

        if (exists) return;

        // 3️⃣ Save alert
        Alert alert = new Alert();
        alert.setProduct(product);
        alert.setAlertType("LOW_STOCK");
        alert.setMessage(
                "Low stock detected: '" + product.getName() +
                        "' (Qty: " + product.getQuantity() +
                        ", Reorder Level: " + product.getReorderLevel() + ")"
        );
        alert.setActive(true);
        alert.setTriggeredAt(LocalDateTime.now());

        alertRepository.save(alert);

        // 4️⃣ Send email to admins
        List<String> admins = userService.getAllAdminEmails();

        String subject = "⚠️ Low Stock Alert";
        String body =
                "Product: " + product.getName() + "\n" +
                        "Current Quantity: " + product.getQuantity() + "\n" +
                        "Reorder Level: " + product.getReorderLevel();

        emailService.sendLowStockAlert(product, admins);

    }

    public void resolveLowStockAlert(Long productId) {

        alertRepository
                .findFirstByProductProductIdAndAlertTypeAndIsActive(
                        productId,
                        "LOW_STOCK",
                        true
                )
                .ifPresent(alert -> {
                    alert.setActive(false);
                    alertRepository.save(alert);
                });
    }


}
