package com.inventra.auth.inventory.repository;

import com.inventra.auth.inventory.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    long count();
}
