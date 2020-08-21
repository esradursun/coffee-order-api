package com.starbux.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDrinkRequest {

    @NotBlank(message = "Drink name is mandatory")
    private String drinkName;

    private List<String> toppingNameList;
}
