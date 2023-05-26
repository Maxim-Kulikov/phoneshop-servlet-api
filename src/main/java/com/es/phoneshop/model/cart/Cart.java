package com.es.phoneshop.model.cart;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@Getter
@Setter
public class Cart implements Serializable {
    private List<CartItem> items;
    private int totalQuantity;
    private BigDecimal totalCost;

    public Cart() {
        items = new ArrayList<>();
        totalQuantity = 0;
        totalCost = new BigDecimal(0);
    }

    @Override
    public String toString() {
        return "Cart[" + items + "]";
    }
}
