package com.starbux.order.repository;

import com.starbux.order.model.ReportMostUsedToppings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportMostUsedToppingsRepository extends JpaRepository<ReportMostUsedToppings, String> {

    List<ReportMostUsedToppings> findAllByOrderByToppingCountDesc();

    Optional<ReportMostUsedToppings> findByDrinkIdAndToppingId(String drinkId, String toppingId);
}
