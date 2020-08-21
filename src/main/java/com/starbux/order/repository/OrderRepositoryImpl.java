package com.starbux.order.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepositoryImpl {
    private static final Logger logger = LoggerFactory.getLogger(OrderRepositoryImpl.class);

    @PersistenceContext
    EntityManager entityManager;

    public Map<String, Double> getTotalAmountOfOrdersPerCustomer() {
        Query query = entityManager.createQuery("SELECT customerName, sum(orderFee.totalPrice) from Orders GROUP BY customerName");

        Map<String, Double> result = new HashMap<>();

        try {
            List<Object[]> queryResult = query.getResultList();
            queryResult.forEach(a -> result.put(a[0].toString(), Double.parseDouble(a[1].toString())));
        } catch (Exception e) {
            logger.error("Invalid query result conversion");
        }

        return result;

    }
}
