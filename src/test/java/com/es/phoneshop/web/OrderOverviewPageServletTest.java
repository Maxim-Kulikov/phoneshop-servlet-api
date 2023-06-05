package com.es.phoneshop.web;

import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.service.OrderService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderOverviewPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private OrderService orderService;
    @InjectMocks
    private OrderOverviewPageServlet servlet = new OrderOverviewPageServlet();

    @Test
    public void testDoGet() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/1");
        when(orderService.getOrderBySecureId(anyString())).thenReturn(new Order());
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("order"), any());
        verify(request).getRequestDispatcher(eq("/WEB-INF/pages/orderOverview.jsp"));
        verify(requestDispatcher).forward(request, response);
    }
}