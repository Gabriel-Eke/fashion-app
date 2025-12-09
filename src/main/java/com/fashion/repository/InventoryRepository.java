package com.fashion.repository;

import java.util.List;
import com.fashion.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    List<Inventory> findByUserId(Long userId);
}
