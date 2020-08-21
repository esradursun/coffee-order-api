package com.starbux.order.repository;

import com.starbux.order.model.OrderProducts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductsRepository extends JpaRepository<OrderProducts, String> {


}
