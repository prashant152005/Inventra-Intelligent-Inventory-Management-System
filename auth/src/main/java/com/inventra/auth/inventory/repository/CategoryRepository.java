package com.inventra.auth.inventory.repository;

import com.inventra.auth.inventory.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    long count();
}
