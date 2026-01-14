package com.inventra.auth.inventory.controller;

import com.inventra.auth.dto.CategoryResponse;
import com.inventra.auth.inventory.entity.Category;
import com.inventra.auth.inventory.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    // ADMIN only
    @PostMapping("/add")
    public Category add(@RequestBody Category category) {
        return service.addCategory(category);
    }

    // ADMIN + EMPLOYEE — returns DTO with id + name
    @GetMapping("/all")
    public List<CategoryResponse> all() {
        return service.getAll()
                .stream()
                .map(c -> {
                    CategoryResponse dto = new CategoryResponse();
                    dto.setId(c.getId());
                    dto.setName(c.getName());
                    return dto;
                })
                .toList();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteCategory(@PathVariable String name) {
        service.deleteByName(name);
        return ResponseEntity.ok().build();
    }
}
