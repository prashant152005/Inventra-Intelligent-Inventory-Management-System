package com.inventra.auth.repository;

import com.inventra.auth.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT a FROM Alert a WHERE a.isActive = true AND a.alertType = 'LOW_STOCK'")
    List<Alert> findActiveLowStockAlerts();

    // Fixed method name: use 'IsActive' to match field name 'isActive'
    List<Alert> findByProductProductIdAndIsActive(Long productId, boolean isActive);
}