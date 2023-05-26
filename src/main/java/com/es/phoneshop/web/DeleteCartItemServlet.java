package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.DefaultCartService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DeleteCartItemServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.INSTANCE;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = parseProductId(request);
        Cart cart = cartService.getCart(request);

        cartService.delete(cart, productId);

        response.sendRedirect(request.getContextPath() + "/cart?message=Cart item removed successfully");
    }

    private Long parseProductId(HttpServletRequest req) {
        String productInfo = req.getPathInfo().substring(1);
        return Long.valueOf(productInfo);
    }

}
