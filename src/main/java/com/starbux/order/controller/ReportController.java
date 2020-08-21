package com.starbux.order.controller;

import com.starbux.order.dto.ReportCustomerTotalAmountDto;
import com.starbux.order.dto.ReportMostUsedToppingsDto;
import com.starbux.order.service.ReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/report")
@Api(description = "This Report API provides 2 reports which one is most used toppings of drink report and the other one is total amount of per customer report.")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final ReportService reportService;


    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/most-used-toppings")
    @ApiOperation(value = "This endpoint provides to get most used topping for drinks", produces = "Application/JSON", response = ReportMostUsedToppingsDto.class, httpMethod = "GET")
    public ResponseEntity<List<ReportMostUsedToppingsDto>> getMostUsedToppingsOfDrinksReport() {
        logger.info("Most used toppings of drinks report is creating");
        return ResponseEntity.ok(reportService.getMostUsedToppingsOfDrinksReport());
    }

    @GetMapping("/get-total-amount-of-orders")
    @ApiOperation(value = "This endpoint provides to get total amounts of the orders per customer", produces = "Application/JSON", response = ReportCustomerTotalAmountDto.class, httpMethod = "GET")
    public ResponseEntity<List<ReportCustomerTotalAmountDto>> getTotalAmountOfOrdersPerCustomerReport() {
        logger.info("The total amount of orders per customer report is creating");
        return ResponseEntity.ok(reportService.getTotalAmountOfOrdersPerCustomerReport());
    }

}
