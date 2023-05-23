package com.es.phoneshop.service.impl;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.repository.OrderDao;
import com.es.phoneshop.repository.impl.OrderDaoImpl;
import com.es.phoneshop.service.OrderService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public enum OrderServiceImpl implements OrderService {
    INSTANCE;

    private OrderDao orderDao;
    private final Lock readLock;
    private final Lock writeLock;

    OrderServiceImpl(){
        orderDao = OrderDaoImpl.instance();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        readLock = readWriteLock.readLock();
        writeLock = readWriteLock.writeLock();
    }

    @Override
    public Order getOrder(Cart cart) {
        try {
            readLock.lock();
            Order order = new Order();
            order.setItems(cart.getItems().stream()
                    .map(CartItem::clone)
                    .collect(Collectors.toList()));
            order.setSubtotal(cart.getTotalCost());
            order.setDeliveryCost(calculateDeliveryCost());
            order.setTotalCost(order.getSubtotal().add(order.getDeliveryCost()));

            return order;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return Arrays.asList(PaymentMethod.values());
    }

    @Override
    public void placeOrder(Order order) {
        try{
            writeLock.lock();
            order.setSecureId(UUID.randomUUID().toString());
            orderDao.save(order);
        }finally{
            writeLock.unlock();
        }
    }

    @Override
    public Order getOrder(Long id) {
        return orderDao.get(id);
    }

    @Override
    public Order getOrderBySecureId(String secureId) {
        return orderDao.get(secureId);
    }

    private BigDecimal calculateDeliveryCost(){
        return new BigDecimal(5);
    }
}


