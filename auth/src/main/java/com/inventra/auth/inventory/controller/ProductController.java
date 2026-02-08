package com.inventra.auth.inventory.controller;

import com.inventra.auth.dto.ProductRequest;
import com.inventra.auth.inventory.entity.Product;
import com.inventra.auth.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody ProductRequest req) {
        return ResponseEntity.ok(service.addProduct(req));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> updateProduct(@RequestBody ProductRequest req) {
        return ResponseEntity.ok(service.updateProduct(req));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<Product>> lowStockProducts() {
        return ResponseEntity.ok(service.getLowStockProducts());
    }

    @DeleteMapping("/delete/{sku}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable String sku) {
        service.deleteProduct(sku);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @PostMapping("/{id}/stock-in")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")

    public ResponseEntity<Product> stockIn(
            @PathVariable Long id,
            @RequestParam int quantity,
            @RequestParam String expiryDate
    ) {
        return ResponseEntity.ok(
                service.stockIn(id, quantity, LocalDate.parse(expiryDate))
        );
    }

    @PostMapping("/{id}/stock-out")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<Product> stockOut(
            @PathVariable Long id,
            @RequestParam int quantity

    ) {
        return ResponseEntity.ok(service.stockOut(id, quantity));
    }
}
