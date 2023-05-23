package com.es.phoneshop.repository.impl;

import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.repository.GenericDao;
import com.es.phoneshop.repository.OrderDao;

public final class OrderDaoImpl extends GenericDao<Order> implements OrderDao {
    private static OrderDaoImpl instance;

    private OrderDaoImpl(){
    }

    public static OrderDaoImpl instance(){
        if(instance == null){
            instance = new OrderDaoImpl();
        }
        return instance;
    }

    @Override
    public Order get(String secureId) throws OrderNotFoundException {
        return getAll().stream()
                .filter(order -> order.getSecureId().equals(secureId))
                .findAny()
                .orElseThrow(() -> new OrderNotFoundException(secureId));
    }
}
