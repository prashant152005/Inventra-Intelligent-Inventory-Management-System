package com.inventra.auth.inventory.controller;

import com.inventra.auth.inventory.entity.Supplier;
import com.inventra.auth.inventory.repository.SupplierRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*") // important for frontend
public class SupplierController {

    private final SupplierRepository repo;

    public SupplierController(SupplierRepository repo) {
        this.repo = repo;
    }

    // ✅ GET all suppliers
    @GetMapping
    public List<Supplier> getAll() {
        return repo.findAll();
    }

    // ✅ GET supplier count
    @GetMapping("/count")
    public long getSupplierCount() {
        return repo.count();
    }

    // ✅ POST add supplier (ADMIN only – secured in SecurityConfig)
    @PostMapping
    public Supplier addSupplier(@RequestBody Supplier supplier) {
        return repo.save(supplier);
    }

    // ✅ DELETE supplier
    @DeleteMapping("/{id}")
    public void deleteSupplier(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
