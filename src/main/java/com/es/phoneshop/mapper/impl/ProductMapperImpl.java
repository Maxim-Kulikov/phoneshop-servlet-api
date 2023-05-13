package com.es.phoneshop.mapper.impl;

import com.es.phoneshop.dto.ViewedProductDto;
import com.es.phoneshop.mapper.ProductMapper;
import com.es.phoneshop.model.product.Product;

public class ProductMapperImpl implements ProductMapper {
    @Override
    public ViewedProductDto toRecentlyViewedProduct(Product product) {
        return new ViewedProductDto(
                product.getId(),
                product.getImageUrl(),
                product.getDescription(),
                product.getCurrency(),
                product.getPrice());
    }
}
