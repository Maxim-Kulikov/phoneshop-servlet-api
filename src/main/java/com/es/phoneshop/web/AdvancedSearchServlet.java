package com.es.phoneshop.web;

import com.es.phoneshop.model.product.DescriptionSearchStrategy;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.repository.ProductDao;
import com.es.phoneshop.repository.impl.ArrayListProductDao;
import com.es.phoneshop.util.NumberValidator;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdvancedSearchServlet extends HttpServlet {
    protected static final String ADVANCED_SEARCH_JSP = "/WEB-INF/pages/advancedSearch.jsp";
    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.instance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> errors = new HashMap<>();
        String strategy = request.getParameter("descriptionSearchStrategy");
        Locale locale = request.getLocale();
        String description = request.getParameter("description");

        BigDecimal minPrice = null;
        try {
            minPrice = NumberValidator.getPriceIfValid(request.getParameter("minPrice"), locale);
        } catch (ParseException | NumberFormatException e) {
            errors.put("minPrice", "IncorrectNumber");
        }

        BigDecimal maxPrice = null;
        try {
            maxPrice = NumberValidator.getPriceIfValid(request.getParameter("maxPrice"), locale);
        } catch (ParseException | NumberFormatException e) {
            errors.put("maxPrice", "IncorrectNumber");
        }

        request.setAttribute("errors", errors);
        if(errors.isEmpty()) {
            request.setAttribute("foundProducts", getFoundProducts(description, strategy, minPrice, maxPrice));
        }
        request.setAttribute("descriptionSearchStrategies", DescriptionSearchStrategy.values());
        request.getRequestDispatcher(ADVANCED_SEARCH_JSP).forward(request, response);
    }

    private List<Product> getFoundProducts(String description, String strategy, BigDecimal minPrice, BigDecimal maxPrice) {
        if(strategy == null) {
            strategy = String.valueOf(DescriptionSearchStrategy.ALL_WORDS);
        }
        if(description == null) {
            description = "";
        }
        Boolean flag = strategy.equals(DescriptionSearchStrategy.valueOf("ALL_WORDS"));
        if(maxPrice.equals("0")) {
            maxPrice = new BigDecimal(10000);
        }
        return productDao.findProductsByNameAndBetweenPrices(description, minPrice, maxPrice, flag);
    }
}
