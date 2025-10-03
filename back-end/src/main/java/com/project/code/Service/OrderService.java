package com.project.code.Service;

import java.time.LocalDateTime;

import org.apache.catalina.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.code.Model.Customer;
import com.project.code.Model.Inventory;
import com.project.code.Model.OrderDetails;
import com.project.code.Model.OrderItem;
import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.PurchaseProductDTO;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderDetailsRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Repo.StoreRepository;

@Service
public class OrderService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;


    public boolean saveOrder(PlaceOrderRequestDTO placeOrderRequest) {

        String email = placeOrderRequest.getCustomerEmail();

        Customer customer = customerRepository.findByEmail(email)
                .orElseGet(() -> customerRepository
                        .save(new Customer(placeOrderRequest.getCustomerName(), email,
                                placeOrderRequest.getCustomerPhone())));

        try {
            var store = storeRepository.findById(placeOrderRequest.getStoreId())
                    .orElseThrow(() -> new Exception("Store not found"));

            OrderDetails orderDetails = new OrderDetails(customer, store, placeOrderRequest.getTotalPrice(),
                    LocalDateTime.now());

            orderDetailsRepository.save(orderDetails);

            for (PurchaseProductDTO product : placeOrderRequest.getPurchaseProduct()) {
                // Get inventory item
                Inventory inventory = inventoryRepository.findByProductIdandStoreId(product.getId(),
                        placeOrderRequest.getStoreId());
                if (inventory == null) {
                    throw new Exception("Inventory not found for product ID: " + product.getId());
                }

                // Update stock levels
                int newStockLevel = inventory.getStockLevel() - product.getQuantity();
                if (newStockLevel < 0) {
                    throw new Exception("Insufficient stock for product ID: " + product.getId());
                }
                inventory.setStockLevel(newStockLevel);
                inventoryRepository.save(inventory);

                // Create and save OrderItem
                OrderItem orderItem = new OrderItem(orderDetails, inventory.getProduct(), product.getQuantity(),
                        inventory.getProduct().getPrice());
                orderItemRepository.save(orderItem);

            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
