package com.es.phoneshop.model.cart;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class Cart {
    private final List<CartItem> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public void add(CartItem cartItem) {
        Long productId = cartItem.getProduct().getId();
        Optional<CartItem> optionalCartItem = getOptionalOfCartItem(productId);
        if (optionalCartItem.isEmpty()) {
            items.add(cartItem);
        } else {
            optionalCartItem.get().addQuantity(cartItem.getQuantity());
        }
    }

    public Optional<CartItem> getOptionalOfCartItem(Long id) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(id))
                .findAny();
    }

    @Override
    public String toString() {
        return "Cart[" + items + "]";
    }
}
