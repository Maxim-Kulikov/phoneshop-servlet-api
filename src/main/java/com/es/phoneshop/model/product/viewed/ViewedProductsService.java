package com.es.phoneshop.model.product.viewed;

import com.es.phoneshop.dto.ViewedProductDto;
import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ViewedProductsService {
    List<ViewedProductDto> getViewedProducts(HttpServletRequest request);

    List<ViewedProductDto> addViewedProduct(List<ViewedProductDto> viewedProducts, Product product);
}
