package com.es.phoneshop.web;

import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.repository.ProductDao;
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
public class PriceHistoryPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ProductDao productDao;
    @InjectMocks
    private PriceHistoryPageServlet servlet = new PriceHistoryPageServlet();

    @Test
    public void testIfDoGetWorksCorrectly() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("1");
        when(productDao.get(1L)).thenReturn(new Product());
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("product"), any());
        verify(request).getRequestDispatcher(eq("/WEB-INF/pages/priceHistory.jsp"));
        verify(requestDispatcher).forward(request, response);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testIfIdIncorrectThenThrowException() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("1");
        when(productDao.get(anyLong())).thenThrow(ProductNotFoundException.class);
        servlet.doGet(request, response);
    }
}
