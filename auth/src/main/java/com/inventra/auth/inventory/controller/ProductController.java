package com.inventra.auth.inventory.controller;

import com.inventra.auth.dto.ProductRequest;
import com.inventra.auth.inventory.entity.Product;
import com.inventra.auth.inventory.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // ADMIN: Add product
//    @PostMapping("/add")
//    public Product addProduct(@RequestBody Product product) {
//        System.out.println(">>> ADD PRODUCT API HIT");
//        return service.addProduct(product);
//    }

    @PostMapping("/add")
    public Product addProduct(@RequestBody ProductRequest req) {
        return service.addProduct(req);
    }


    // ADMIN + EMPLOYEE: View products
    @GetMapping("/all")
    public List<Product> getProducts() {
        return service.getAllProducts();
    }

    // ADMIN: Update stock only
    @PutMapping("/update-stock/{id}")
    public Product updateStock(
            @PathVariable Long id,
            @RequestParam int quantity
    ) {
        return service.updateQuantity(id, quantity);
    }

    // ADMIN: Full Product Update (Edit modal)
    @PutMapping("/update")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        Product updated = service.updateProduct(product);
        return ResponseEntity.ok(updated);
    }

    // LOW STOCK LIST
    @GetMapping("/low-stock")
    public List<Product> lowStockProducts() {
        return service.getLowStockProducts();
    }
    @DeleteMapping("/delete/{sku}")
    public ResponseEntity<String> deleteProduct(@PathVariable String sku) {
        service.deleteProduct(sku);
        return ResponseEntity.ok("Product deleted successfully");
    }

    }
