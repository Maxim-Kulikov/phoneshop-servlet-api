package com.es.phoneshop.mapper;

import com.es.phoneshop.dto.ViewedProductDto;
import com.es.phoneshop.model.product.Product;

public interface ProductMapper {
    ViewedProductDto toRecentlyViewedProduct(Product product);
}
