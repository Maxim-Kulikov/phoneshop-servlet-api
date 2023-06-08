package com.es.phoneshop.repository;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;

import java.math.BigDecimal;
import java.util.List;

public abstract class ProductDao extends GenericDao<Product> {
    public abstract List<Product> findProductsByNameAndSort(String query, SortField sortField, SortOrder sortOrder);
    public abstract List<Product> findProductsByNameAndBetweenPrices(String query, BigDecimal minPrice, BigDecimal maxPrice, Boolean isByAllWards);
}
