package com.inventra.auth.repository;

import com.inventra.auth.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findByTransactionTypeAndTransactionDateBetween(String out, LocalDateTime localDateTime, LocalDateTime localDateTime1);
}