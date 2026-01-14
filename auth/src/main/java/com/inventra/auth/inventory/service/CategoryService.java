package com.inventra.auth.inventory.service;

import com.inventra.auth.inventory.entity.Category;
import com.inventra.auth.inventory.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public Category addCategory(Category category) {
        if (repo.existsByName(category.getName())) {
            throw new RuntimeException("Category already exists");
        }
        return repo.save(category);
    }

    public List<Category> getAll() {
        return repo.findAll();
    }

    public void deleteByName(String name) {
        Category c = repo.findByName(name);
        if (c != null) {
            repo.delete(c);
        }
    }
}
