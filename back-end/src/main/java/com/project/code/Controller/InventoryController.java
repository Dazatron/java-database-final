package com.project.code.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceClass serviceClass;

    @PutMapping("/updateInventory")
    public Map<String, String> updateInventory(@RequestBody CombinedRequest combinedRequest) {
        try {
            // vaidtae product ID
            if (!serviceClass.ValidateProductId(combinedRequest.getProduct().getId())) {
                return Map.of("message", "Product ID is not valid");
            }

            // If valid update the inventory
            var inventory = serviceClass.getInventoryId(combinedRequest.getInventory());
            if (inventory != null) {
                inventory.setStockLevel(combinedRequest.getInventory().getStockLevel());
                inventoryRepository.save(inventory);
                return Map.of("message", "Inventory updated successfully");
            } else {
                return Map.of("message", "No data available");
            }
        } catch (DataIntegrityViolationException e) {
            return Map.of("message", "Data integrity violation: " + e.getMessage());
        }

    }

    @PostMapping("/saveInventory")
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
        try {
            if (!serviceClass.validateInventory(inventory)) {
                return Map.of("message", "Inventory already exists");
            }

            // Save inventory
            inventoryRepository.save(inventory);
            return Map.of("message", "Inventory saved successfully");
        } catch (DataIntegrityViolationException e) {
            return Map.of("message", "Data integrity violation: " + e.getMessage());
        }

    }

    @GetMapping("/{storeid}")
    public Map<String, Object> getAllProducts(@PathVariable Long storeId) {
        var products = productRepository.findProductsByStoreId(storeId);
        return Map.of("products", products);
    }

    @GetMapping("filter/{category}/{name}/{storeid}")
    public Map<String, Object> getProductName(@PathVariable String category, @PathVariable String name,
            @PathVariable Long storeId) {

        var products = productRepository.findByNameLike(storeId, name);
        if (products.isEmpty()) {
            products = productRepository.findByCategory(category);
        }

        if (products.isEmpty()) {
            var product = productRepository.findByName(name);
            products.add(product);
        }

        return Map.of("product", products);
    }

    @GetMapping("/search/{name}/{storeid}")
    public Map<String, Object> searchProduct(@PathVariable String name, @PathVariable Long storeId) {
        var products = productRepository.findByNameLike(storeId, name);
        return Map.of("product", products);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(@PathVariable Long id) {
        try {
            if (!serviceClass.ValidateProductId(id)) {
                return Map.of("message", "Product ID is not valid");
            }

            // Delete product
            var product = productRepository.findById(id).get();
            productRepository.delete(product);

            // Delete inventory
            inventoryRepository.deleteByProductId(id);

            return Map.of("message", "Product deleted successfully");
        } catch (DataIntegrityViolationException e) {
            return Map.of("message", "Data integrity violation: " + e.getMessage());
        }

    }

    @GetMapping("validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(@PathVariable int quantity, @PathVariable Long storeId,
            @PathVariable Long productId) {
        var inventory = inventoryRepository.findByProductIdandStoreId(productId, storeId);
        if (inventory != null && inventory.getStockLevel() >= quantity) {
            return true;
        }
        return false;
    }

}
