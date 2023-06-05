package com.es.phoneshop.web;

import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.repository.impl.ArrayListProductDao;
import com.es.phoneshop.repository.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class PriceHistoryPageServlet extends HttpServlet {
    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.productDao = ArrayListProductDao.instance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, ProductNotFoundException {
        Long id = Long.valueOf(req.getParameter("id"));
        req.setAttribute("product", productDao.get(id));
        req.getRequestDispatcher("/WEB-INF/pages/priceHistory.jsp").forward(req, resp);
    }
}
