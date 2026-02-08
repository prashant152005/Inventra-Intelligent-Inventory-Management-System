package com.inventra.auth.inventory.controller;

import com.inventra.auth.inventory.repository.CategoryRepository;
import com.inventra.auth.inventory.repository.ProductRepository;
import com.inventra.auth.inventory.repository.SupplierRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final SupplierRepository supplierRepo;

    public DashboardController(ProductRepository productRepo,
                               CategoryRepository categoryRepo,
                               SupplierRepository supplierRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.supplierRepo = supplierRepo;
    }

    @GetMapping("/counts")
    public Map<String, Long> getCounts() {
        Map<String, Long> data = new HashMap<>();
        data.put("products", productRepo.count());
        data.put("categories", categoryRepo.count());
        data.put("suppliers", supplierRepo.count());
        return data;
    }
}
