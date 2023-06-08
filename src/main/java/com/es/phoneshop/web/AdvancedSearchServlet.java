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
import java.util.*;

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
        request.setAttribute("descriptionSearchStrategies", Arrays.asList(DescriptionSearchStrategy.values()));
        request.getRequestDispatcher(ADVANCED_SEARCH_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> errors = new HashMap<>();
        String strategy = request.getParameter("descriptionSearchStrategy");
        Locale locale = request.getLocale();
        String description = request.getParameter("description");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");

        BigDecimal minPrice = putPriceValueElseError(minPriceStr, "minPrice", errors, locale);
        BigDecimal maxPrice = putPriceValueElseError(maxPriceStr, "maxPrice", errors, locale);

        request.setAttribute("errors", errors);
        if (errors.isEmpty()) {
            request.setAttribute("foundProducts", getFoundProducts(description, strategy, minPrice, maxPrice));
        }

        doGet(request, resp);
    }

    private List<Product> getFoundProducts(String description, String strategy, BigDecimal minPrice, BigDecimal maxPrice) {
        if (strategy == null) {
            strategy = String.valueOf(DescriptionSearchStrategy.ALL_WORDS);
        }
        Boolean flag = strategy.equals(DescriptionSearchStrategy.ALL_WORDS.toString());
        return productDao.findProductsByNameAndBetweenPrices(description, minPrice, maxPrice, flag);
    }

    private BigDecimal putPriceValueElseError(String priceStr, String key, Map<String, String> errors, Locale locale) {
        BigDecimal price = null;
        try {
            price = priceStr == null || priceStr.isBlank() ? null : NumberValidator.getPriceIfValid(priceStr, locale);
        } catch (ParseException | NumberFormatException e) {
            errors.put(key, "IncorrectNumber");
        }
        return price;
    }
}
