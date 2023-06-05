package com.es.phoneshop.service;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import jakarta.servlet.http.HttpServletRequest;

public interface CartService {
    Cart getCart(HttpServletRequest request);

    void add(Cart cart, Long productId, int quantity) throws OutOfStockException;

    void update(Cart cart, Long productId, int quantity) throws OutOfStockException;

    void delete(Cart cart, Long productId);

    void clear(Cart cart);
}
