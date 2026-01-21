package com.inventra.auth.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SalesReportDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private int totalTransactions;
    private List<TopProductDto> topProducts;
    private List<DailySalesDto> dailySales;

    // Constructors
    public SalesReportDto() {}

    // Getters & Setters
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public List<TopProductDto> getTopProducts() {
        return topProducts;
    }

    public void setTopProducts(List<TopProductDto> topProducts) {
        this.topProducts = topProducts;
    }

    public List<DailySalesDto> getDailySales() {
        return dailySales;
    }

    public void setDailySales(List<DailySalesDto> dailySales) {
        this.dailySales = dailySales;
    }
}