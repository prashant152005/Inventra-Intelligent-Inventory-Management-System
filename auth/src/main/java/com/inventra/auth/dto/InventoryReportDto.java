package com.inventra.auth.dto;

import java.math.BigDecimal;
import java.util.List;

public class InventoryReportDto {

    private int totalProducts;
    private BigDecimal totalStockValue;
    private int lowStockCount;
    private List<LowStockProductDto> lowStockProducts;
    private int outOfStockCount;

    // Constructors
    public InventoryReportDto() {}

    // Getters & Setters
    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public BigDecimal getTotalStockValue() {
        return totalStockValue;
    }

    public void setTotalStockValue(BigDecimal totalStockValue) {
        this.totalStockValue = totalStockValue;
    }

    public int getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(int lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public List<LowStockProductDto> getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(List<LowStockProductDto> lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public int getOutOfStockCount() {
        return outOfStockCount;
    }

    public void setOutOfStockCount(int outOfStockCount) {
        this.outOfStockCount = outOfStockCount;
    }
}