package com.inventra.auth.inventory.controller;

import com.inventra.auth.inventory.entity.StockTransaction;
import com.inventra.auth.inventory.entity.StockTransactionBatch;
import com.inventra.auth.inventory.repository.StockTransactionBatchRepository;
import com.inventra.auth.inventory.repository.StockTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class StockTransactionController {

    private final StockTransactionRepository stockTransactionRepository;
    private final StockTransactionBatchRepository stockTransactionBatchRepository;
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public List<StockTransaction> getAllTransactions() {
        return stockTransactionRepository.findAll();
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public List<StockTransaction> getByProduct(@PathVariable Long productId) {
        return stockTransactionRepository.findByProduct_ProductId(productId);
    }
    @GetMapping("/{transactionId}/batches")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public List<StockTransactionBatch> getBatchUsage(
            @PathVariable Long transactionId
    ) {
        return stockTransactionBatchRepository
                .findByTransaction_Id(transactionId);
    }

}
