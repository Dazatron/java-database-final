package com.project.code.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.code.Model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    public List<Product> findAll();

    public List<Product> findByCategory(String category);

    public List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    public Product findBySku(String sku);

    public Product findByName(String name);

    public List<Product> findProductsByStoreId(Long storeid);

    @Query("SELECT p FROM Product p JOIN Inventory i ON p.id = i.product.id WHERE i.store.id = :storeId AND p.name LIKE CONCAT('%', :pname, '%')")
    List<Product> findByNameLike(@Param("storeId") Long storeId, @Param("pname") String pname);

}
