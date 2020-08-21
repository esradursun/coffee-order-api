package com.starbux.order.repository;

import com.starbux.order.model.OrderFee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderFeeRepository extends JpaRepository<OrderFee, String> {
}
