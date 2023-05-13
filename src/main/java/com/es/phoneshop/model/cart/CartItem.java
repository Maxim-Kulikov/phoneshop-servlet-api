package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CartItem {
    private Product product;
    private int quantity = 0;

    @Override
    public String toString() {
        return product.getCode() + ", " + quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }
}
