package com.inventra.auth.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inventra.auth.entity.Alert;
import com.inventra.auth.entity.StockTransaction;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String description;

    private Double unitPrice;

    private Integer quantity;

    private Integer reorderLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // New relationships - with @JsonIgnore to prevent circular JSON serialization
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore  // ← Critical fix: prevents infinite recursion in JSON
    private List<StockTransaction> stockTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore  // ← Critical fix: prevents infinite recursion in JSON
    private List<Alert> alerts = new ArrayList<>();

    // Getters & Setters (existing + new)
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<StockTransaction> getStockTransactions() { return stockTransactions; }
    public void setStockTransactions(List<StockTransaction> stockTransactions) { this.stockTransactions = stockTransactions; }

    public List<Alert> getAlerts() { return alerts; }
    public void setAlerts(List<Alert> alerts) { this.alerts = alerts; }
}