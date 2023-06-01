package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CartItem implements Serializable, Cloneable {
    private Product product;
    private int quantity = 0;

    @Override
    public String toString() {
        return product.getCode() + ", " + quantity;
    }

    @Override
    public CartItem clone() {
        try {
            return (CartItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
