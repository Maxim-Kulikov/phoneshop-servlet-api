package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.OrderService;
import com.es.phoneshop.service.impl.DefaultCartService;
import com.es.phoneshop.service.impl.OrderServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CheckoutPageServlet extends HttpServlet {
    protected static final String CHECKOUT_JSP = "/WEB-INF/pages/checkout.jsp";
    private CartService cartService;
    private OrderService orderService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.INSTANCE;
        orderService = OrderServiceImpl.INSTANCE;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        Order order = orderService.getOrder(cart);
        request.setAttribute("order", order);
        request.setAttribute("paymentMethods", orderService.getPaymentMethods());
        request.getRequestDispatcher(CHECKOUT_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        Order order = orderService.getOrder(cart);
        Predicate<String> lettersPredicate = p -> p.chars().allMatch(Character::isLetter);
        Predicate<String> phonePredicate = p -> p.trim().chars().allMatch(Character::isDigit);

        Map<String, String> errors = new HashMap<>();
        setIfValidString("firstName", request, errors, order::setFirstName, lettersPredicate);
        setIfValidString("lastName", request, errors, order::setLastName, lettersPredicate);
        setIfValidString("phone", request, errors, s -> {
            s = s.trim();
            order.setPhone(s);
        }, phonePredicate);
        setDeliveryAddress(request, errors, order);
        setPaymentMethod(request, errors, order);
        setDeliveryDate(request, errors, order);

        if (errors.isEmpty()) {
            orderService.placeOrder(order);
            cartService.clear(cart);
            response.sendRedirect(request.getContextPath() + "/order/overview/" + order.getSecureId());
            return;
        }

        request.setAttribute("errors", errors);
        doGet(request, response);
    }

    private void setIfValidString(String name,
                                  HttpServletRequest request,
                                  Map<String, String> errors,
                                  Consumer<String> consumer,
                                  Predicate<String> predicate) {
        String param = request.getParameter(name);

        if (param.isBlank()) {
            errors.put(name, "Value is required");
            return;
        }

        if (predicate.test(param)) {
            consumer.accept(param);
        } else {
            if ("phone".equals(name)) {
                errors.put(name, "Only digits are required");
            } else {
                errors.put(name, "Only letters are required");
            }
        }
    }

    private void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String param = request.getParameter("paymentMethod");

        if (param.isBlank()) {
            errors.put("paymentMethod", "Value is required");
        } else {
            order.setPaymentMethod(PaymentMethod.valueOf(param));
        }
    }

    private void setDeliveryDate(HttpServletRequest request, Map<String, String> errors, Order order) {
        String param = request.getParameter("deliveryDate");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

        if (param.isBlank()) {
            errors.put("deliveryDate", "Value is required");
            return;
        }

        try {
            Date date = format.parse(param);
            if (new Date().after(date)) {
                errors.put("deliveryDate", "Nearest delivery on the next day");
                return;
            }
            order.setDeliveryDate(date);
        } catch (ParseException e) {
            errors.put("deliveryDate", "Wrong date format");
        }
    }

    private void setDeliveryAddress(HttpServletRequest request, Map<String, String> errors, Order order) {
        String param = request.getParameter("deliveryAddress");

        if (!param.isBlank()) {
            order.setDeliveryAddress(param);
        } else {
            errors.put("deliveryAddress", "Value is required");
        }
    }


}
