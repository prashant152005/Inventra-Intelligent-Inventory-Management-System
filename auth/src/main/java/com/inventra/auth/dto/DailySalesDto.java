package com.inventra.auth.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailySalesDto {

    private LocalDate date;
    private BigDecimal revenue;
    private int transactions;

    // Constructors
    public DailySalesDto() {}

    // Getters & Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public int getTransactions() {
        return transactions;
    }

    public void setTransactions(int transactions) {
        this.transactions = transactions;
    }
}