package com.starbux.order.repository;

import com.starbux.order.model.OrderDrinkTopping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDrinkToppingRepository extends JpaRepository<OrderDrinkTopping, String> {

}
