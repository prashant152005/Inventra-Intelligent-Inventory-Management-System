package com.inventra.auth.inventory.service;

import com.inventra.auth.dto.ProductRequest;
import com.inventra.auth.inventory.entity.*;
import com.inventra.auth.inventory.repository.*;
import com.inventra.auth.service.AlertService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inventra.auth.inventory.enums.TransactionType;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Service
@Transactional
public class ProductService {

    private final ProductRepository repo;
    private final AlertService alertService;
    private final CategoryRepository categoryRepo;
    private final SupplierRepository supplierRepo;
    private final StockTransactionRepository stockTransactionRepository;

    @Autowired
    private BatchRepository batchRepo;

    @Autowired
    private BatchService batchService;

    @Autowired
    private ProductRepository productRepository;
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public ProductService(ProductRepository repo,
                          AlertService alertService,
                          CategoryRepository categoryRepo,
                          SupplierRepository supplierRepo,
                          StockTransactionRepository stockTransactionRepository) {

        this.repo = repo;
        this.alertService = alertService;
        this.categoryRepo = categoryRepo;
        this.supplierRepo = supplierRepo;
        this.stockTransactionRepository = stockTransactionRepository;
    }
    public Product addProduct(ProductRequest req) {

        if (repo.existsBySku(req.getSku())) {
            throw new RuntimeException("SKU already exists");
        }

        if (req.getCategoryId() == null) {
            throw new RuntimeException("Category is required");
        }

        if (req.getSupplierId() == null) {
            throw new RuntimeException("Supplier is required");
        }

        Product p = new Product();
        p.setSku(req.getSku());
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setUnitPrice(req.getUnitPrice());
        p.setReorderLevel(req.getReorderLevel());
        p.setQuantity(req.getQuantity() != null ? req.getQuantity() : 0);

        p.setCategory(
                categoryRepo.findById(req.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found"))
        );

        Supplier supplier = supplierRepo.findById(req.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        p.setSupplier(supplier);


        // ✅ Save product FIRST
        Product savedProduct = repo.save(p);

        // ✅ Create FIRST batch
        Batch batch = new Batch();
        batch.setBatchNumber("BATCH-" + System.currentTimeMillis());
        batch.setExpiryDate(req.getExpiryDate());
        batch.setQuantity(req.getQuantity());
        batch.setSupplierName(supplier.getName());
        batch.setProduct(savedProduct);

        batchRepo.save(batch);

        return savedProduct;
    }



    public Product updateProduct(ProductRequest req) {

        Product p = repo.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        p.setName(req.getName());
        p.setReorderLevel(req.getReorderLevel());
        p.setUnitPrice(req.getUnitPrice());
        p.setDescription(req.getDescription());

        return repo.save(p);
    }
    public Product stockIn(Long productId, int qty, LocalDate expiryDate) {

        Product product = repo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update total product quantity
        int currentQty = product.getQuantity() == null ? 0 : product.getQuantity();
        product.setQuantity(currentQty + qty);
        repo.save(product);

        // 🔕 RESOLVE LOW-STOCK ALERT IF STOCK RECOVERED
        if (product.getQuantity() > product.getReorderLevel()) {
            alertService.resolveLowStockAlert(product.getProductId());
        }

        // ✅ Create NEW batch
        Batch batch = new Batch();
        batch.setBatchNumber("BATCH-" + System.currentTimeMillis());
        batch.setExpiryDate(expiryDate);
        batch.setQuantity(qty);
        batch.setSupplierName(product.getSupplier().getName());
        batch.setProduct(product);

        batchRepo.save(batch);

        // Stock transaction log
        StockTransaction tx = new StockTransaction();
        tx.setProduct(product);
        tx.setQuantity(qty);
        tx.setTransactionType(TransactionType.STOCK_IN);
        tx.setPerformedAt(LocalDateTime.now());
        tx.setPerformedBy("ADMIN");
        tx.setBatchNumber(batch.getBatchNumber());

        stockTransactionRepository.save(tx);

        return product;
    }


    public Product stockOut(Long productId, int qty) {

        Product product = repo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int currentQty = product.getQuantity() == null ? 0 : product.getQuantity();

        if (currentQty < qty) {
            throw new RuntimeException("Insufficient stock");
        }

        // 🔥 FIFO batch consumption
        batchService.consumeStockFIFO(product, qty);

        // Update product total quantity
        product.setQuantity(currentQty - qty);
        repo.save(product);

        // 🔔 LOW STOCK ALERT
        alertService.checkLowStock(product);

        // 🔔 CRITICAL (LOW + NEAR EXPIRY)
        alertService.checkCombinedLowStockAndExpiry(product);

        // Stock transaction log
        StockTransaction tx = new StockTransaction();
        tx.setProduct(product);
        tx.setQuantity(qty);
        tx.setTransactionType(TransactionType.STOCK_OUT);
        tx.setPerformedAt(LocalDateTime.now());
        tx.setPerformedBy("ADMIN");
        tx.setBatchNumber("FIFO");

        stockTransactionRepository.save(tx);

        return product;
    }





    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public List<Product> getLowStockProducts() {
        return repo.findLowStockProducts();
    }

    public void deleteProduct(String sku) {
        Product p = repo.findBySku(sku);
        if (p != null) {
            repo.delete(p);
        }
    }

}
