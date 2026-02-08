package com.inventra.auth.inventory.controller;

import com.inventra.auth.inventory.entity.Batch;
import com.inventra.auth.inventory.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/batches")
public class BatchController {

    @Autowired
    private BatchService service;

    @GetMapping("/expired")
    public List<Batch> expiredBatches() {
        return service.getExpiredBatches();
    }

    @GetMapping("/near-expiry")
    public List<Batch> nearExpiryBatches() {
        return service.getNearExpiryBatches();
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public List<Batch> batchesByProduct(@PathVariable Long productId) {
        return service.getBatchesByProduct(productId);
    }


}
