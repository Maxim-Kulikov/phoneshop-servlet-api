package com.es.phoneshop.service;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.repository.OrderDao;
import com.es.phoneshop.service.impl.OrderServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {
    @Mock
    private OrderDao orderDao;
    @InjectMocks
    private OrderService orderService = OrderServiceImpl.INSTANCE;

    @Test
    public void testGetOrderFromCard() {
        assertNotNull(orderService.getOrder(new Cart()));
    }

    @Test
    public void getPaymentMethods() {
        assertEquals(Arrays.asList(PaymentMethod.values()), orderService.getPaymentMethods());
    }

    @Test
    public void checkOrderPlacing() {
        Order order = new Order();

        orderService.placeOrder(order);

        verify(orderDao).save(eq(order));
        assertNotNull(order.getSecureId());
    }
}
