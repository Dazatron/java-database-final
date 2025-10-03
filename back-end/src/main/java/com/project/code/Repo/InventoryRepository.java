package com.project.code.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.project.code.Model.Inventory;

import jakarta.transaction.Transactional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    public Inventory findByProductIdAndStoreId(Long productId, Long storeId);

    public List<Inventory> findByStore_Id(Long storeId);

    @Modifying
    @Transactional
    public void deleteByProductId(Long productId);

}
