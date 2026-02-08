package com.inventra.auth.inventory.repository;

import com.inventra.auth.inventory.entity.StockTransaction;
import com.inventra.auth.inventory.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StockTransactionRepository
        extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findByProduct_ProductId(Long productId);

    List<StockTransaction>
    findByTransactionTypeAndPerformedAtBetween(
            TransactionType transactionType,
            LocalDateTime start,
            LocalDateTime end
    );
    List<StockTransaction>
    findByProductProductIdOrderByPerformedAtDesc(Long productId);

}
