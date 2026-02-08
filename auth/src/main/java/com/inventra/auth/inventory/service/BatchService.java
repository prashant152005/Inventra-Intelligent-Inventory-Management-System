package com.inventra.auth.inventory.service;

import com.inventra.auth.inventory.entity.Batch;
import com.inventra.auth.inventory.repository.BatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inventra.auth.inventory.entity.Product;
import com.inventra.auth.inventory.enums.BatchStatus;
import org.springframework.scheduling.annotation.Scheduled;



import java.time.LocalDate;
import java.util.List;

@Service
public class BatchService {

    @Autowired
    private BatchRepository batchRepo;

    /**
     * FIFO batch consumption (skips expired batches)
     */
    public void consumeStockFIFO(Product product, int requiredQty) {

        List<Batch> batches =
                batchRepo.findByProductProductIdOrderByExpiryDateAsc(
                        product.getProductId()
                );

        int remainingQty = requiredQty;

        for (Batch batch : batches) {

            // 🚫 Skip expired batches
            if (batch.getExpiryDate().isBefore(LocalDate.now())) {
                continue;
            }

            if (remainingQty <= 0) break;

            int available = batch.getQuantity();

            if (available <= 0) continue;

            if (available >= remainingQty) {
                batch.setQuantity(available - remainingQty);
                remainingQty = 0;
            } else {
                remainingQty -= available;
                batch.setQuantity(0);
            }

            batchRepo.save(batch);
        }

        if (remainingQty > 0) {
            throw new RuntimeException(
                    "Insufficient non-expired stock for product: " + product.getName()
            );
        }
    }

    public List<Batch> getExpiredBatches() {
        return batchRepo.findByExpiryDateBefore(LocalDate.now());
    }

    public List<Batch> getNearExpiryBatches() {
        return batchRepo.findByExpiryDateBetween(
                LocalDate.now(),
                LocalDate.now().plusDays(7)
        );
    }

//    @Scheduled(cron = "0 0 1 * * ?") // runs daily at 1 AM
//    public void updateBatchExpiryStatus() {
//
//        LocalDate today = LocalDate.now();
//        LocalDate nearExpiryLimit = today.plusDays(7);
//
//        List<Batch> allBatches = batchRepo.findAll();
//
//        for (Batch batch : allBatches) {
//
//            if (batch.getExpiryDate().isBefore(today)) {
//                batch.setStatus(BatchStatus.EXPIRED);
//            }
//            else if (!batch.getExpiryDate().isAfter(nearExpiryLimit)) {
//                batch.setStatus(BatchStatus.NEAR_EXPIRY);
//            }
//            else {
//                batch.setStatus(BatchStatus.ACTIVE);
//            }
//
//            batchRepo.save(batch);
//        }
//    }
public BatchStatus calculateStatus(LocalDate expiryDate, int quantity) {
    LocalDate today = LocalDate.now();

    if (expiryDate.isBefore(today)) {
        return BatchStatus.EXPIRED;
    }

    if (quantity == 0) {
        return BatchStatus.OUT_OF_STOCK;
    }

    if (!expiryDate.isAfter(today.plusDays(7))) {
        return BatchStatus.NEAR_EXPIRY;
    }

    return BatchStatus.ACTIVE;
}
    public List<Batch> getBatchesByProduct(Long productId) {
        List<Batch> batches =
                batchRepo.findByProductProductIdOrderByExpiryDateAsc(productId);

        batches.forEach(b ->
                b.setStatus(
                        calculateStatus(b.getExpiryDate(), b.getQuantity())
        ));

        return batches;
    }


}
