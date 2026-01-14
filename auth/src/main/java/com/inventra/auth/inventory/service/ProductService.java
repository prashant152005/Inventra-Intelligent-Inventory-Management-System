package com.inventra.auth.inventory.service;

import com.inventra.auth.dto.ProductRequest;
import com.inventra.auth.inventory.entity.Product;
import com.inventra.auth.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepo;

    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public Product addProduct(ProductRequest req) {
        Product p = new Product();
        p.setSku(req.getSku());
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setQuantity(req.getQuantity());
        p.setReorderLevel(req.getReorderLevel());
        p.setUnitPrice(req.getUnitPrice());
        return productRepo.save(p);
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product updateQuantity(Long id, int quantity) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setQuantity(quantity);
        return productRepo.save(product);
    }

    public List<Product> getLowStockProducts() {
        return productRepo.findByQuantityLessThanEqual(5);
    }

    public Product updateProduct(Product product) {
        Product existing = productRepo.findBySku(product.getSku());
        if (existing == null) {
            throw new RuntimeException("Product not found");
        }

        existing.setName(product.getName());
        existing.setQuantity(product.getQuantity());
        existing.setReorderLevel(product.getReorderLevel());
        existing.setUnitPrice(product.getUnitPrice());
        existing.setDescription(product.getDescription());

        return productRepo.save(existing);
    }

    public void deleteProduct(String sku) {
        Product existing = productRepo.findBySku(sku);
        if (existing == null) {
            throw new RuntimeException("Product not found");
        }
        productRepo.delete(existing);
    }
}
