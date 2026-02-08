package com.inventra.auth.inventory.service;

import com.inventra.auth.dto.StockInRequest;
import com.inventra.auth.dto.StockOutRequest;
import com.inventra.auth.inventory.entity.Batch;
import com.inventra.auth.inventory.entity.Product;
import com.inventra.auth.inventory.entity.StockTransaction;
import com.inventra.auth.inventory.entity.StockTransactionBatch;
import com.inventra.auth.inventory.repository.BatchRepository;
import com.inventra.auth.inventory.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.inventra.auth.inventory.repository.StockTransactionRepository;
import com.inventra.auth.inventory.enums.TransactionType;
import com.inventra.auth.inventory.repository.StockTransactionBatchRepository;



import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private StockTransactionRepository stockTransactionRepository;

    @Autowired
    private StockTransactionBatchRepository stockTransactionBatchRepository;

    @Transactional
    public void stockIn(StockInRequest request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Batch batch = batchRepository
                .findByProductProductIdAndBatchNumber(
                        request.getProductId(),
                        request.getBatchNumber()
                )
                .orElseGet(() -> {
                    Batch newBatch = new Batch();
                    newBatch.setBatchNumber(request.getBatchNumber());
                    newBatch.setExpiryDate(request.getExpiryDate());
                    newBatch.setSupplierName(request.getSupplierName());
                    newBatch.setQuantity(0);
                    newBatch.setProduct(product);
                    return newBatch;
                });

        batch.setQuantity(batch.getQuantity() + request.getQuantity());
        batchRepository.save(batch);

        // ✅ CREATE TRANSACTION LOG
        StockTransaction tx = new StockTransaction();
        tx.setTransactionType(TransactionType.STOCK_IN);
        tx.setQuantity(request.getQuantity());
        tx.setBatchNumber(batch.getBatchNumber());
        tx.setPerformedBy(getCurrentUsername());
        tx.setProduct(product);

        stockTransactionRepository.save(tx);

        // 🔁 Recalculate product quantity
        int totalQuantity = batchRepository
                .findByProductProductIdOrderByExpiryDateAsc(product.getProductId())
                .stream()
                .mapToInt(Batch::getQuantity)
                .sum();

        product.setQuantity(totalQuantity);
        productRepository.save(product);
    }


    @Transactional
    public void stockOut(StockOutRequest request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int remaining = request.getQuantity();

        // 1️⃣ Create STOCK_OUT transaction FIRST
        StockTransaction tx = new StockTransaction();
        tx.setTransactionType(TransactionType.STOCK_OUT);
        tx.setQuantity(request.getQuantity());
        tx.setPerformedBy(getCurrentUsername());
        tx.setProduct(product);

        tx = stockTransactionRepository.saveAndFlush(tx);



        // 2️⃣ FIFO batches
        List<Batch> batches =
                batchRepository.findByProductProductIdOrderByExpiryDateAsc(
                        product.getProductId()
                );

        for (Batch batch : batches) {

            if (remaining <= 0) break;
            if (batch.getQuantity() <= 0) continue;

            int used;

            if (batch.getQuantity() >= remaining) {
                used = remaining;
                batch.setQuantity(batch.getQuantity() - remaining);
                remaining = 0;
            } else {
                used = batch.getQuantity();
                remaining -= batch.getQuantity();
                batch.setQuantity(0);
            }

            batchRepository.save(batch);

            // 3️⃣ SAVE batch usage
            StockTransactionBatch usage = new StockTransactionBatch();
            usage.setTransaction(tx);
            usage.setBatch(batch);
            usage.setQuantityUsed(used);

            stockTransactionBatchRepository.saveAndFlush(usage);

        }

        if (remaining > 0) {
            throw new RuntimeException("Insufficient stock");
        }

        // 4️⃣ Recalculate product quantity
        int totalQty = batchRepository
                .findByProductProductIdOrderByExpiryDateAsc(product.getProductId())
                .stream()
                .mapToInt(Batch::getQuantity)
                .sum();

        product.setQuantity(totalQty);
        productRepository.save(product);
    }

    private String getCurrentUsername() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
    public List<StockTransaction> getTransactionsByProduct(Long productId) {
        return stockTransactionRepository
                .findByProductProductIdOrderByPerformedAtDesc(productId);
    }

}
