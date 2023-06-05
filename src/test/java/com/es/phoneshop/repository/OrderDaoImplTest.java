package com.es.phoneshop.repository;

import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.repository.impl.DefaultOrderDao;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderDaoImplTest {
    private OrderDao orderDao;

    @Before
    public void init(){
        orderDao = DefaultOrderDao.instance();
        orderDao.deleteAll();
    }

    @Test(expected = OrderNotFoundException.class)
    public void getOrderByIdIfExistedOtherwiseThrowException(){
        Order order = new Order();
        orderDao.save(order);

        assertEquals(order, orderDao.get(1L));
        orderDao.get(2L);
    }

    @Test(expected = OrderNotFoundException.class)
    public void getOrderBySecureIdIfExistedOtherwiseThrowException(){
        Order order = new Order();
        order.setSecureId("xxx");
        orderDao.save(order);

        assertEquals(order, orderDao.get("xxx"));
        orderDao.get(2L);
    }
}
