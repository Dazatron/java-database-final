package com.project.code.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;

@Service
public class ServiceClass {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    public boolean validateInventory(Inventory inventory) {
        var inv = inventoryRepository.findByProductIdAndStoreId(inventory.getProduct().getId(),
                inventory.getStore().getId());
        return inv == null;
    }

    public boolean validateProduct(Product product) {
        var prod = productRepository.findByName(product.getName());
        return prod == null;
    }

    public boolean ValidateProductId(long id) {
        return productRepository.findById(id).isPresent();
    }

    public Inventory getInventoryId(Inventory inventory) {
        return inventoryRepository.findByProductIdAndStoreId(inventory.getProduct().getId(),
                inventory.getStore().getId());
    }

}
