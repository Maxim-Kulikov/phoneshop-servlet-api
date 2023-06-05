package com.es.phoneshop.service.impl;

import com.es.phoneshop.dto.ViewedProductDto;
import com.es.phoneshop.mapper.ProductMapper;
import com.es.phoneshop.mapper.impl.ProductMapperImpl;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.ViewedProductsService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public enum ViewedProductsServiceImpl implements ViewedProductsService {
    INSTANCE;

    private final String VIEWED_SESSION_ATTRIBUTE = "viewedProducts";
    private final Lock writeLock;
    private final ProductMapper productMapper;

    ViewedProductsServiceImpl() {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        writeLock = readWriteLock.writeLock();
        productMapper = new ProductMapperImpl();
    }

    @Override
    public List<ViewedProductDto> getViewedProducts(HttpServletRequest request) {
        List<ViewedProductDto> viewedProducts =
                (List<ViewedProductDto>) request.getSession().getAttribute(VIEWED_SESSION_ATTRIBUTE);
        if (viewedProducts == null) {
            viewedProducts = new ArrayList<>();
            request.getSession().setAttribute(VIEWED_SESSION_ATTRIBUTE, viewedProducts);
        }
        return viewedProducts;

    }

    @Override
    public List<ViewedProductDto> addViewedProduct(List<ViewedProductDto> list, Product product) {
        try {
            writeLock.lock();
            list.add(0, productMapper.toRecentlyViewedProduct(product));
            return list.stream()
                    .distinct()
                    .limit(3)
                    .collect(Collectors.toList());
        } finally {
            writeLock.unlock();
        }
    }
}
