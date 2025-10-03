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

import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceClass serviceClass;

    @Autowired
    private InventoryRepository inventoryRepository;

    @PostMapping("/addProduct")
    public Map<String, String> addProduct(@RequestBody Product product) {
        try {
            if (!serviceClass.validateProduct(product)) {
                return Map.of("message", "Product already exists");
            }
            productRepository.save(product);
            return Map.of("message", "Product added successfully");
        } catch (DataIntegrityViolationException e) {
            return Map.of("message", "Data integrity violation: " + e.getMessage());
        }
    }

    @GetMapping("/product/{id}")
    public Map<String, Object> getProductbyId(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);
        return Map.of("products", product);
    }

    @PutMapping("/updateProduct")
    public Map<String, String> updateProduct(@RequestBody Product product) {
        try {
            productRepository.save(product);
            return Map.of("message", "Product updated successfully");
        } catch (DataIntegrityViolationException e) {
            return Map.of("message", "Data integrity violation: " + e.getMessage());
        }
    }

    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct(@PathVariable String name, @PathVariable String category) {
        var products = productRepository.findAll();

        if (!name.equals("null") && !category.equals("null")) {
            products = productRepository.findByCategory(category).stream()
                    .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase())).toList();
        } else if (!name.equals("null")) {
            products = productRepository.findByNameLike(0L, name); // Assuming storeId is not relevant here
        } else if (!category.equals("null")) {
            products = productRepository.findByCategory(category);
        }

        return Map.of("products", products);
    }

    @GetMapping("/list")
    public Map<String, Object> listProduct() {
        var products = productRepository.findAll();
        return Map.of("products", products);
    }

    @GetMapping("filter/{category}/{storeid}")
    public Map<String, Object> getProductbyCategoryAndStoreId(@PathVariable String category,
            @PathVariable Long storeId) {
        var products = productRepository.findByCategory(category).stream()
                .filter(p -> inventoryRepository.findByProductIdAndStoreId(p.getId(), storeId) != null).toList();
        return Map.of("products", products);
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

    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {
        var products = productRepository.findByNameLike(0L, name); // Assuming storeId is not relevant here
        return Map.of("products", products);
    }

}
