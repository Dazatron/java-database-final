package com.project.code.Model;

public class CombinedRequest {
    
    private Product product;
    private Inventory inventory;

    public Product getProduct() {
        return product;
    }

    // Setter for product
    public void setProduct(Product product) {
        this.product = product;
    }

    // Getter for inventory
    public Inventory getInventory() {
        return inventory;
    }

    // Setter for inventory
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}