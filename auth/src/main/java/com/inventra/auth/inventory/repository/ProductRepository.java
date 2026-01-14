package com.inventra.auth.inventory.repository;

import com.inventra.auth.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findBySku(String sku);
    List<Product> findByQuantityLessThanEqual(Integer quantity);
}
