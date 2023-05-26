package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.OrderService;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private CartService cartService;
    @Mock
    private OrderService orderService;
    private static final String CHECKOUT_JSP = "/WEB-INF/pages/checkout.jsp";
    @InjectMocks
    private final CheckoutPageServlet servlet = new CheckoutPageServlet();

    @Before
    public void init() throws ServletException {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).getRequestDispatcher(eq(CHECKOUT_JSP));
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("order"), any());
        verify(request).setAttribute(eq("paymentMethods"), any());
    }

    @Test
    public void testDoPostPlaceOrderAndClearCartIfValidData() throws IOException, ServletException {
        when(cartService.getCart(request)).thenReturn(new Cart());
        when(orderService.getOrder((Cart) any())).thenReturn(new Order());
        when(request.getParameter("firstName")).thenReturn("Maks");
        when(request.getParameter("lastName")).thenReturn("Maks");
        when(request.getParameter("phone")).thenReturn("1111");
        when(request.getParameter("deliveryAddress")).thenReturn("xxx");
        when(request.getParameter("paymentMethod")).thenReturn(PaymentMethod.CREDIT_CARD.toString());
        when(request.getParameter("deliveryDate")).thenReturn(deliveryDate(1));

        servlet.doPost(request, response);
        verify(response).sendRedirect(anyString());
        //verify(request).setAttribute(eq("errors"), any());
    }

    @Test
    public void testDoPostPutsErrorsIfInvalidData() throws IOException, ServletException {
        when(cartService.getCart(request)).thenReturn(new Cart());
        when(orderService.getOrder((Cart) any())).thenReturn(new Order());
        when(request.getParameter("firstName")).thenReturn("");
        when(request.getParameter("lastName")).thenReturn("Maks");
        when(request.getParameter("phone")).thenReturn("+1111");
        when(request.getParameter("deliveryAddress")).thenReturn("xxx");
        when(request.getParameter("paymentMethod")).thenReturn(PaymentMethod.CREDIT_CARD.toString());
        when(request.getParameter("deliveryDate")).thenReturn(deliveryDate(-1));

        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
    }

    private String deliveryDate(int bias) {
        Date now = new Date();
        String deliveryDate = new SimpleDateFormat("dd.MM.yyyy").format(now);
        String day = String.valueOf((Integer.parseInt(deliveryDate.substring(0, 2))+bias));
        return day + deliveryDate.substring(2);
    }
}