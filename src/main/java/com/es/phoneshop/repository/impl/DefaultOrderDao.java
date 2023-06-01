package com.es.phoneshop.repository.impl;

import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.repository.OrderDao;

public final class DefaultOrderDao extends OrderDao {
    private static DefaultOrderDao instance;

    private DefaultOrderDao() {
    }

    public static DefaultOrderDao instance() {
        if (instance == null) {
            instance = new DefaultOrderDao();
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
