package com.inventra.auth.inventory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stock_transaction_batches")
public class StockTransactionBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Parent transaction
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private StockTransaction transaction;

    // 🔗 Batch used
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    // 🔢 Quantity taken from this batch
    @Column(nullable = false)
    private int quantityUsed;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public StockTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(StockTransaction transaction) {
        this.transaction = transaction;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public int getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(int quantityUsed) {
        this.quantityUsed = quantityUsed;
    }
}
