package com.es.phoneshop.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ViewedProductDto {
    private Long id;
    private String imageUrl;
    private String description;
    private Currency currency;
    private BigDecimal price;
}
