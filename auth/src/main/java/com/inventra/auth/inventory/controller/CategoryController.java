package com.inventra.auth.inventory.controller;

import com.inventra.auth.inventory.entity.Category;
import com.inventra.auth.inventory.repository.CategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository repo;

    public CategoryController(CategoryRepository repo) {
        this.repo = repo;
    }

    // ✅ GET all categories
    @GetMapping
    public List<Category> getAll() {
        return repo.findAll();
    }

    // ✅ GET category count
    @GetMapping("/count")
    public long getCategoryCount() {
        return repo.count();
    }

    // ✅ POST add category  🔥 REQUIRED
    @PostMapping
    public Category addCategory(@RequestBody Category category) {
        return repo.save(category);
    }

    // ✅ DELETE category
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
