package com.starbux.order.service;

import com.starbux.order.dto.ReportCustomerTotalAmountDto;
import com.starbux.order.dto.ReportMostUsedToppingsDto;
import com.starbux.order.dto.ReportMostUsedToppingsDtoConverter;
import com.starbux.order.model.ReportMostUsedToppings;
import com.starbux.order.repository.OrderRepositoryImpl;
import com.starbux.order.repository.ReportMostUsedToppingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final OrderRepositoryImpl orderRepository;
    private final ReportMostUsedToppingsRepository reportMostUsedToppingsRepository;
    private final ReportMostUsedToppingsDtoConverter reportMostUsedToppingsDtoConverter;

    public ReportService(OrderRepositoryImpl orderRepository, ReportMostUsedToppingsRepository reportMostUsedToppingsRepository, ReportMostUsedToppingsDtoConverter reportMostUsedToppingsDtoConverter) {
        this.orderRepository = orderRepository;
        this.reportMostUsedToppingsRepository = reportMostUsedToppingsRepository;
        this.reportMostUsedToppingsDtoConverter = reportMostUsedToppingsDtoConverter;
    }

    public List<ReportMostUsedToppingsDto> getMostUsedToppingsOfDrinksReport() {
        List<ReportMostUsedToppings> mostUsedToppingsList = new ArrayList<>();
        reportMostUsedToppingsRepository.findAllByOrderByToppingCountDesc().forEach(m -> {
            if (mostUsedToppingsList.stream().noneMatch(t -> t.getDrink().equals(m.getDrink()))) {
                mostUsedToppingsList.add(m);
            }
        });
        return mostUsedToppingsList.stream().map(reportMostUsedToppingsDtoConverter::convert).collect(Collectors.toList());
    }

    public List<ReportCustomerTotalAmountDto> getTotalAmountOfOrdersPerCustomerReport() {
        Map<String, Double> totalAmountOfOrdersPerCustomer = orderRepository.getTotalAmountOfOrdersPerCustomer();
        return totalAmountOfOrdersPerCustomer.entrySet()
                .stream()
                .map(totalAmount -> ReportCustomerTotalAmountDto.builder()
                        .customerName(totalAmount.getKey()).totalPrice(totalAmount.getValue()).build())
                .collect(Collectors.toList());
    }
}
