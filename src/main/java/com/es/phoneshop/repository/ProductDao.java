package com.es.phoneshop.repository;

import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;

import java.util.List;

public interface ProductDao {
    Product get(Long id) throws ProductNotFoundException;

    List<Product> findProductsByNameAndSort(String query, SortField sortField, SortOrder sortOrder);

    void save(Product product);

    void delete(Long id);

    void deleteAll();
}
