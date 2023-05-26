package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MiniCartServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private CartService cartService;
    @InjectMocks
    private MiniCartServlet servlet = new MiniCartServlet();


    @Test
    public void testService() throws ServletException, IOException {
        when(cartService.getCart(request)).thenReturn(new Cart());
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.service(request, response);
        Mockito.verify(request).setAttribute(eq("cart"), any());
        Mockito.verify(request).getRequestDispatcher(eq("/WEB-INF/pages/miniCart.jsp"));
        Mockito.verify(requestDispatcher).include(request, response);
    }
}