package com.inventra.auth.inventory.controller;

import com.inventra.auth.dto.StockInRequest;
import com.inventra.auth.dto.StockOutRequest;
import com.inventra.auth.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;


    @PostMapping("/stock-in/{productId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> stockIn(
            @PathVariable Long productId,
            @RequestBody StockInRequest request
    ) {
        // ✅ Validate first
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        if (request.getExpiryDate() == null) {
            throw new IllegalArgumentException("Expiry date is required");
        }

        // ✅ Inject productId into request
        request.setProductId(productId);

        // ✅ Generate batch number if not provided
        if (request.getBatchNumber() == null || request.getBatchNumber().isBlank()) {
            request.setBatchNumber("BATCH-" + System.currentTimeMillis());
        }

        inventoryService.stockIn(request);

        return ResponseEntity.ok("Stock added successfully");
    }



    @PostMapping("/stock-out")
    public ResponseEntity<String> stockOut(@RequestBody StockOutRequest request) {
        inventoryService.stockOut(request);
        return ResponseEntity.ok("Stock removed successfully");
    }
    @GetMapping("/transactions/{productId}")
    public ResponseEntity<?> getTransactions(@PathVariable Long productId) {
        return ResponseEntity.ok(
                inventoryService.getTransactionsByProduct(productId)
        );
    }


}
