package com.es.phoneshop.repository;

import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.model.order.Order;

public abstract class OrderDao extends GenericDao<Order> {
    public abstract Order get(String secureId) throws OrderNotFoundException;
}
