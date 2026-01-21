package com.inventra.auth.inventory.controller;

import com.inventra.auth.dto.ProductRequest;
import com.inventra.auth.inventory.entity.Product;
import com.inventra.auth.inventory.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // Add Product
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody ProductRequest req) {
        Product saved = service.addProduct(req);
        return ResponseEntity.ok(saved);
    }

    // Get All Products
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = service.getAllProducts();
        return ResponseEntity.ok(products);  // This forces application/json
    }

    // Update Product
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        return ResponseEntity.ok(service.updateProduct(product));
    }

    // Low Stock Products
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<Product>> lowStockProducts() {
        List<Product> lowStock = service.getLowStockProducts();
        return ResponseEntity.ok(lowStock);  // Ensures JSON
    }

    // Delete Product by SKU
    @DeleteMapping("/delete/{sku}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable String sku) {
        service.deleteProduct(sku);
        return ResponseEntity.ok("Product deleted successfully");
    }

    // New: Stock In
    @PostMapping("/{id}/stock-in")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> stockIn(@PathVariable Long id, @RequestParam int quantity, @RequestParam(required = false) String remarks) {
        service.stockIn(id, quantity, remarks);
        return ResponseEntity.ok("Stock in recorded");
    }

    // New: Stock Out
    @PostMapping("/{id}/stock-out")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> stockOut(@PathVariable Long id, @RequestParam int quantity, @RequestParam(required = false) String remarks) {
        service.stockOut(id, quantity, remarks);
        return ResponseEntity.ok("Stock out recorded");
    }
}