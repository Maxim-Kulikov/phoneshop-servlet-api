package com.es.phoneshop.model.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class PriceInfo implements Serializable {
    private final LocalDate date;
    private final BigDecimal price;
}
