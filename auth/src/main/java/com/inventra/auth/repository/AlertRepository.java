package com.inventra.auth.repository;

import com.inventra.auth.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT a FROM Alert a WHERE a.isActive = true AND a.alertType = 'LOW_STOCK'")
    List<Alert> findActiveLowStockAlerts();

    List<Alert> findByProductProductIdAndIsActive(
            Long productId,
            boolean isActive
    );

    List<Alert> findByProductProductIdAndAlertTypeAndIsActive(
            Long productId,
            String alertType,
            boolean isActive
    );

    Optional<Alert> findFirstByProductProductIdAndAlertTypeAndIsActive(
            Long productId,
            String alertType,
            boolean isActive
    );

    boolean existsByProductProductIdAndAlertTypeAndIsActive(
            Long productId,
            String alertType,
            boolean isActive
    );

    List<Alert> findByAlertTypeAndIsActive(
            String alertType,
            boolean isActive
    );
}
