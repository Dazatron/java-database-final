package com.project.code.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.ReviewRepository;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/{storeId}/{productId}")
    public Map<String, Object> getReviews(@PathVariable Long storeId, @PathVariable Long productId) {
        var reviews = reviewRepository.findByStoreIdAndProductId(storeId, productId);
        var filteredReviews = reviews.stream().map(review -> {
            var customer = customerRepository.findById(review.getCustomerId()).orElse(null);
            var customerName = customer != null ? customer.getName() : "Unknown";
            return Map.of(
                    "comment", review.getComment(),
                    "rating", review.getRating(),
                    "customerName", customerName);
        }).toList();
        return Map.of("reviews", filteredReviews);
    }

}
