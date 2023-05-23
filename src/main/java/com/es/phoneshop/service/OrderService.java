package com.es.phoneshop.service;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;

import java.util.List;

public interface OrderService {
    Order getOrder(Cart cart);
    List<PaymentMethod> getPaymentMethods();
    void placeOrder(Order order);
    Order getOrder(Long id);
    Order getOrderBySecureId(String secureId);
}
