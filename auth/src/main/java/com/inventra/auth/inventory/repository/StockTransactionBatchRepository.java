package com.inventra.auth.inventory.repository;

import com.inventra.auth.inventory.entity.StockTransactionBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockTransactionBatchRepository
        extends JpaRepository<StockTransactionBatch, Long> {

    // 🔍 View batches used for a transaction
    List<StockTransactionBatch>
    findByTransaction_Id(Long transactionId);

    // 🔍 View batch usage per product (optional, useful later)
    List<StockTransactionBatch>
    findByBatch_Product_ProductId(Long productId);
}
