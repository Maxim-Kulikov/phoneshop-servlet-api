package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;
import com.es.phoneshop.repository.ProductDao;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ViewedProductsService;
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
public class ProductListPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ProductDao productDao;
    @Mock
    private ViewedProductsService viewedProductsService;
    @Mock
    private CartService cartService;
    @InjectMocks
    private ProductListPageServlet servlet = new ProductListPageServlet();

    @Before
    public void setup() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getParameter("productId")).thenReturn("1");
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        when(request.getParameter("query")).thenReturn("sam");
        when(request.getParameter("sort")).thenReturn("price");
        when(request.getParameter("order")).thenReturn("desc");

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("products"), any());
        verify(productDao).findProductsByNameAndSort(
                eq("sam"),
                eq(SortField.valueOf("price")),
                eq(SortOrder.valueOf("desc")));
        verify(request).setAttribute(eq("viewedProducts"), any());
        verify(request).getRequestDispatcher(eq("/WEB-INF/pages/productList.jsp"));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void ifInputQuantityIncorrectThenThrowException() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("ss");

        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), eq("Incorrect number"));
    }

    @Test
    public void ifNegativeInputQuantityIncorrectThenThrowException() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("-5");

        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), eq("Incorrect number"));
    }

    @Test
    public void ifNotAvailableStockThenThrowException() throws ServletException, OutOfStockException, IOException {
        doThrow(OutOfStockException.class).when(cartService).add(any(), anyLong(), anyInt());
        when(request.getParameter("quantity")).thenReturn("5");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("error"), contains("Out of stock"));
    }

}