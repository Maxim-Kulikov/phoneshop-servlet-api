package com.es.phoneshop.repository;

import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.model.order.Order;

public interface OrderDao {
    Order get(Long id) throws OrderNotFoundException;
    Order get(String secureId) throws OrderNotFoundException;
    void save(Order order) throws OrderNotFoundException;
    void delete(Long id) throws OrderNotFoundException;
    void deleteAll();
}
