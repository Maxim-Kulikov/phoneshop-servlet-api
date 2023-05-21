package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CartPageServlet extends HttpServlet {
    protected static final String CART_JSP = "/WEB-INF/pages/cart.jsp";
    private CartService cartService;
    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.INSTANCE;
        productDao = ArrayListProductDao.INSTANCE;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(CART_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");

        Map<Long, String> errors = new HashMap<>();
        Locale locale = request.getLocale();
        Cart cart = cartService.getCart(request);

        for (int i = 0; i < productIds.length; i++) {
            Long productId = null;
            int quantity;
            try {
                productId = Long.parseLong(productIds[i]);
                quantity = getQuantityIfValid(quantities[i], locale);
                cartService.update(cart, productId, quantity);
            } catch (OutOfStockException e) {
                errors.put(productId,
                        "Available quantity is " + e.getStockAvailable() + ", but requested is " + e.getStockRequested());
            } catch (ParseException | NumberFormatException e) {
                errors.put(productId, "Incorrect number");
            }
        }
        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }
    }

    private int getQuantityIfValid(String quantityStr, Locale locale) throws ParseException {
        int quantity = Integer.parseInt(quantityStr);
        NumberFormat format = NumberFormat.getInstance(locale);
        quantity = format.parse(Integer.toString(quantity)).intValue();
        if (quantity < 1) {
            throw new NumberFormatException();
        }

        return quantity;
    }

}
