package com.es.phoneshop.web;

import com.es.phoneshop.dto.ViewedProductDto;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.DefaultCartService;
import com.es.phoneshop.repository.impl.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.repository.ProductDao;
import com.es.phoneshop.service.ViewedProductsService;
import com.es.phoneshop.service.impl.ViewedProductsServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

public class ProductDetailsPageServlet extends HttpServlet {
    private ProductDao productDao;
    private CartService cartService;
    private ViewedProductsService viewedProductsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.productDao = ArrayListProductDao.instance();
        this.cartService = DefaultCartService.INSTANCE;
        this.viewedProductsService = ViewedProductsServiceImpl.INSTANCE;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, ProductNotFoundException {
        Long id = parseProductId(req);
        Product product = productDao.get(id);
        List<ViewedProductDto> viewedProducts
                = viewedProductsService.getViewedProducts(req);
        viewedProducts = viewedProductsService.addViewedProduct(viewedProducts, product);

        req.getSession().setAttribute("viewedProducts", viewedProducts);
        req.setAttribute("product", product);

        req.getRequestDispatcher("/WEB-INF/pages/productDetails.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long productId = parseProductId(req);

        int quantity = 0;
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

        resp.sendRedirect(req.getContextPath() + "/products/" + productId + "?message=Product added to cart");
    }

    private int getQuantityIfValid(HttpServletRequest request) throws ParseException {
        int quantity;
        String quantityStr = request.getParameter("quantity");

        quantity = Integer.parseInt(quantityStr);
        NumberFormat format = NumberFormat.getInstance(request.getLocale());
        quantity = format.parse(Integer.toString(quantity)).intValue();
        if (quantity < 1) {
            throw new NumberFormatException();
        }

        return quantity;
    }

    private Long parseProductId(HttpServletRequest req) {
        String productInfo = req.getPathInfo().substring(1);
        return Long.valueOf(productInfo);
    }
}

