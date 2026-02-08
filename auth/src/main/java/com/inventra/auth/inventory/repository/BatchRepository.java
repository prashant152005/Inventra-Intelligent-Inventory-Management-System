package com.inventra.auth.inventory.repository;

import com.inventra.auth.inventory.entity.Batch;
import com.inventra.auth.inventory.enums.BatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    // FIFO logic support
    List<Batch> findByProductProductIdOrderByExpiryDateAsc(Long productId);

    Optional<Batch> findByProductProductIdAndBatchNumber(
            Long productId,
            String batchNumber
    );

    List<Batch> findByExpiryDateBefore(LocalDate date);

    List<Batch> findByExpiryDateBetween(LocalDate start, LocalDate end);
    List<Batch> findByStatus(BatchStatus status);

    // BatchRepository.java
    @Query("""
SELECT b FROM Batch b
WHERE b.product.productId = :productId
AND b.expiryDate BETWEEN :today AND :nearDate
AND b.quantity > 0
""")
    List<Batch> findNearExpiryBatchesForProduct(
            Long productId,
            LocalDate today,
            LocalDate nearDate
    );

    List<Batch> findByProductProductIdAndExpiryDateBetween(
            Long productId,
            LocalDate startDate,
            LocalDate endDate
    );

}
