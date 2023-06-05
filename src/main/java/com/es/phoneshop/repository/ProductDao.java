package com.es.phoneshop.repository;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;

import java.util.List;

public abstract class ProductDao extends GenericDao<Product> {
    public abstract List<Product> findProductsByNameAndSort(String query, SortField sortField, SortOrder sortOrder);
}
