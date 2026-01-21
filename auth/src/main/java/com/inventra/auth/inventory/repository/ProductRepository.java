package com.inventra.auth.inventory.repository;

import com.inventra.auth.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);

    Product findBySku(String sku);

    @Query("SELECT p FROM Product p WHERE p.quantity <= p.reorderLevel")
    List<Product> findLowStockProducts();
}