package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.CartService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private CartService cartService;
    private static final String[] productIds = new String[]{"1"};
    private static final String[] quantities = new String[]{"1"};
    private static final String[] incorrectQuantities = new String[]{"x", "0"};
    private static final String CART_JSP = "/WEB-INF/pages/cart.jsp";
    @InjectMocks
    private CartPageServlet servlet = new CartPageServlet();


    @Before
    public void init() {
        when(request.getParameterValues("productId")).thenReturn(productIds);
        when(request.getParameterValues("quantity")).thenReturn(quantities);

        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getLocale()).thenReturn(Locale.US);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).getRequestDispatcher(eq(CART_JSP));
    }

    @Test
    public void testDoPostSendRedirectWhenValidParameters() throws ServletException, IOException {
        servlet.doPost(request, response);

        verify(response).sendRedirect(any());
    }

    @Test
    public void testDoPostPutErrorsWhenIncorrectQuantity() throws ServletException, IOException {
        when(request.getParameterValues("quantity")).thenReturn(incorrectQuantities);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), any());
    }

    @Test
    public void testDoPostPutErrorsWhenProductOutOfStock() throws ServletException, IOException, OutOfStockException {
        doThrow(new OutOfStockException(new Product(), 0, 0)).when(cartService).update(any(), anyLong(), anyInt());

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), any());
    }
}