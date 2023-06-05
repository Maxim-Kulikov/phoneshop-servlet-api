package com.es.phoneshop.web;

import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.service.OrderService;
import com.es.phoneshop.service.impl.OrderServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class OrderOverviewPageServlet extends HttpServlet {
    protected static final String JSP = "/WEB-INF/pages/orderOverview.jsp";
    private OrderService orderService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderService = OrderServiceImpl.INSTANCE;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String secureOrderId = request.getPathInfo().substring(1);
        Order order = orderService.getOrderBySecureId(secureOrderId);
        request.setAttribute("order", order);
        request.getRequestDispatcher(JSP).forward(request, response);
    }
}
