package com.inventra.auth.inventory.service;

import com.inventra.auth.dto.ProductRequest;
import com.inventra.auth.inventory.entity.Product;
import com.inventra.auth.entity.StockTransaction;
import com.inventra.auth.inventory.repository.ProductRepository;
import com.inventra.auth.repository.StockTransactionRepository;
import com.inventra.auth.service.AlertService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;
    private final StockTransactionRepository transactionRepo;
    private final AlertService alertService;

    public ProductService(ProductRepository repo,
                          StockTransactionRepository transactionRepo,
                          AlertService alertService) {
        this.repo = repo;
        this.transactionRepo = transactionRepo;
        this.alertService = alertService;
    }

    public Product addProduct(ProductRequest req) {
        if (repo.existsBySku(req.getSku())) {
            throw new RuntimeException("SKU already exists");
        }

        Product p = new Product();
        p.setSku(req.getSku());
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setUnitPrice(req.getUnitPrice());
        p.setQuantity(req.getQuantity());
        p.setReorderLevel(req.getReorderLevel());

        return repo.save(p);
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product updateProduct(Product product) {
        return repo.save(product);
    }

    public void deleteProduct(String sku) {
        Product p = repo.findBySku(sku);
        if (p != null) {
            repo.delete(p);
        }
    }

    public List<Product> getLowStockProducts() {
        return repo.findLowStockProducts();
    }

    public Product updateQuantity(Long id, int quantity) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        p.setQuantity(quantity);
        return repo.save(p);
    }

    // Stock In Operation – now updates alerts
    public void stockIn(Long productId, int quantity, String remarks) {
        Product product = repo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update stock
        product.setQuantity(product.getQuantity() + quantity);
        repo.save(product);

        // Record transaction
        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setTransactionType("IN");
        transaction.setQuantity(quantity);
        transaction.setRemarks(remarks);
        transactionRepo.save(transaction);

        // Check and update alert status (will resolve if now above reorder level)
        alertService.checkAndUpdateAlertForProduct(product);
    }

    // Stock Out Operation – now updates alerts with latest quantity or creates new one
    public void stockOut(Long productId, int quantity, String remarks) {
        Product product = repo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        // Update stock
        product.setQuantity(product.getQuantity() - quantity);
        repo.save(product);

        // Record transaction
        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setTransactionType("OUT");
        transaction.setQuantity(quantity);
        transaction.setRemarks(remarks);
        transactionRepo.save(transaction);

        // Check and update alert status (creates/updates if low, resolves if not)
        alertService.checkAndUpdateAlertForProduct(product);
    }
}