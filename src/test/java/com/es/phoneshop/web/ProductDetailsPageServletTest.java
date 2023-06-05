package com.es.phoneshop.web;

import com.es.phoneshop.dto.ViewedProductDto;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.repository.ProductDao;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ViewedProductsService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    @Mock
    private HttpSession session;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ProductDao productDao;
    @Mock
    private CartService cartService;
    @Mock
    private ViewedProductsService viewedProductsService;
    @InjectMocks
    private ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @Before
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(request.getPathInfo()).thenReturn("/1");
        when(productDao.get(anyLong())).thenReturn(new Product());
        when(cartService.getCart(request)).thenReturn(any());
        when(viewedProductsService.getViewedProducts(request)).thenReturn(new ArrayList<ViewedProductDto>());
        when(viewedProductsService.addViewedProduct(any(), any())).thenReturn(new ArrayList<ViewedProductDto>());
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
    }

    @Test
    public void testIfDoGetWorksCorrectly() throws ServletException, IOException {

        servlet.doGet(request, response);

        verify(request.getSession()).setAttribute(eq("viewedProducts"), any());
        verify(request).setAttribute(eq("product"), any());
        verify(request).getRequestDispatcher(eq("/WEB-INF/pages/productDetails.jsp"));
        verify(requestDispatcher).forward(request, response);
    }

    @Test(expected = ProductNotFoundException.class)
    public void whenProductNotFoundThrowException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/5");
        when(productDao.get(anyLong())).thenThrow(ProductNotFoundException.class);
        servlet.doGet(request, response);
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
