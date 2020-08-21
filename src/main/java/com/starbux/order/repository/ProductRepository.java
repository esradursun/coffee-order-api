package com.starbux.order.repository;

import com.starbux.order.model.Product;
import com.starbux.order.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    Product findByName(String name);

    List<Product> findAllByProductType(ProductType productType);
}
