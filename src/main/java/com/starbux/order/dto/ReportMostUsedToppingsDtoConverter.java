package com.starbux.order.dto;

import com.starbux.order.model.ReportMostUsedToppings;
import org.springframework.stereotype.Component;

@Component
public class ReportMostUsedToppingsDtoConverter {

    private final ProductDtoConverter productDtoConverter;

    public ReportMostUsedToppingsDtoConverter(ProductDtoConverter productDtoConverter) {
        this.productDtoConverter = productDtoConverter;
    }

    public ReportMostUsedToppingsDto convert(ReportMostUsedToppings from) {

        return ReportMostUsedToppingsDto.builder()
                .drink(productDtoConverter.convert(from.getDrink()))
                .topping(productDtoConverter.convert(from.getTopping()))
                .toppingCount(from.getToppingCount())
                .build();
    }
}
