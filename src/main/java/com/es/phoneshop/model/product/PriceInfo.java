package com.es.phoneshop.model.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class PriceInfo {
    private final String date;
    private final BigDecimal price;
}
