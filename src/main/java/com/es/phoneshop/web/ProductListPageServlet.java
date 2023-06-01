package com.es.phoneshop.web;

import com.es.phoneshop.dto.ViewedProductDto;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.DefaultCartService;
import com.es.phoneshop.repository.impl.ArrayListProductDao;
import com.es.phoneshop.repository.ProductDao;
import com.es.phoneshop.service.ViewedProductsService;
import com.es.phoneshop.service.impl.ViewedProductsServiceImpl;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {
    private ProductDao productDao;
    private ViewedProductsService viewedProductsService;
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.instance();
        viewedProductsService = ViewedProductsServiceImpl.INSTANCE;
        cartService = DefaultCartService.INSTANCE;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        String sortField = request.getParameter("sort");
        String sortOrder = request.getParameter("order");
        request.setAttribute("products", productDao.findProductsByNameAndSort(query,
                Optional.ofNullable(sortField).map(SortField::valueOf).orElse(null),
                Optional.ofNullable(sortOrder).map(SortOrder::valueOf).orElse(null)));
        request.setAttribute("viewedProducts", viewedProducts(request));
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int quantity = 0;
        Long productId = Long.parseLong(req.getParameter("productId"));

        try {
            quantity = getQuantityIfValid(req);
        } catch (ParseException | NumberFormatException e) {
            req.setAttribute("error", "Incorrect number");
            doGet(req, resp);
            return;
        }

        try {
            Cart cart = cartService.getCart(req);
            cartService.add(cart, productId, quantity);
        } catch (OutOfStockException e) {
            req.setAttribute("error", "Out of stock, available " + e.getStockAvailable() + ", but requested " + e.getStockRequested());
            doGet(req, resp);
            return;
        }

        /*req.setAttribute("message", "Product was added successfully");
        doGet(req, resp);*/
        resp.sendRedirect(req.getContextPath() + "/products?productId=" + productId + "&message=Product was added successfully");
    }

    private List<ViewedProductDto> viewedProducts(HttpServletRequest req) {
        List<ViewedProductDto> viewedProducts = viewedProductsService.getViewedProducts(req);
        return viewedProducts;
    }

    private int getQuantityIfValid(HttpServletRequest request) throws ParseException {
        String quantityStr = request.getParameter("quantity");

        int quantity = Integer.parseInt(quantityStr);
        NumberFormat format = NumberFormat.getInstance(request.getLocale());
        quantity = format.parse(Integer.toString(quantity)).intValue();
        if (quantity < 1) {
            throw new NumberFormatException();
        }

        return quantity;
    }

}
