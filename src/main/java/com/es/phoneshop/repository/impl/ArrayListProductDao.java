package com.es.phoneshop.repository.impl;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;
import com.es.phoneshop.repository.ProductDao;
import org.codehaus.plexus.util.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ArrayListProductDao extends ProductDao {
    private static ArrayListProductDao instance;
    private final List<Product> products;
    private final Lock readLock;
    private final Comparator<Product> ascPriceComparator;
    private final Comparator<Product> descPriceComparator;
    private final Comparator<Product> ascDescriptionComparator;
    private final Comparator<Product> descDescriptionComparator;

    public static ArrayListProductDao instance() {
        if (instance == null) {
            instance = new ArrayListProductDao();
        }
        return instance;
    }

    private ArrayListProductDao() {
        products = getAll();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        readLock = readWriteLock.readLock();
        ascPriceComparator = new PriceComparator(SortOrder.asc);
        descPriceComparator = new PriceComparator(SortOrder.desc);
        ascDescriptionComparator = new DescriptionComparator(SortOrder.asc);
        descDescriptionComparator = new DescriptionComparator(SortOrder.desc);
    }

    @Override
    public List<Product> findProductsByNameAndSort(String query, SortField sortField, SortOrder sortOrder) {
        try {
            readLock.lock();
            Stream<Product> stream = filterByStockAndPriceProducts();
            if (StringUtils.isNotBlank(query)) {
                stream = filterByDescription(stream, query);
            }
            if (sortField != null && sortOrder != null) {
                stream = sortProducts(stream, sortField, sortOrder);
            }
            return stream.collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    private Stream<Product> filterByStockAndPriceProducts() {
        return products.stream()
                .filter(this::productInStock)
                .filter(this::productPriceNotNull);
    }

    private Stream<Product> filterByDescription(Stream<Product> stream, String query) {
        String[] queries = split(query);
        return stream
                .filter(product -> filterDescription(product, queries))
                .sorted(new RelevanceComparator(queries));
    }

    private String[] split(String query) {
        return query.trim().split("[\\s]+");
    }

    private Stream<Product> sortProducts(Stream<Product> stream, SortField sortField, SortOrder sortOrder) {
        return stream
                .sorted(sortComparator(sortField, sortOrder));
    }

    private Comparator<Product> sortComparator(SortField sortField, SortOrder sortOrder) {
        if (sortField == SortField.price) {
            if (sortOrder == SortOrder.asc) {
                return ascPriceComparator;
            }
            return descPriceComparator;
        }
        if (sortOrder == SortOrder.asc) {
            return ascDescriptionComparator;
        }
        return descDescriptionComparator;
    }

    private boolean filterDescription(Product product, String[] queries) {
        if (queries.length == 0) {
            return true;
        }
        String description = product.getDescription();
        for (String x : queries) {
            if (description.toLowerCase().contains(x.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private int countNumOfMatchingSubstrings(String description, String[] queries) {
        String formattedDescription = description.toLowerCase();
        return (int) Arrays.stream(queries).filter(query -> formattedDescription.contains(query.toLowerCase())).count();
    }

    private boolean productPriceNotNull(Product product) {
        return product.getPrice() != null;
    }

    private boolean productInStock(Product product) {
        return product.getStock() > 0;
    }

    private record PriceComparator(SortOrder sortOrder) implements Comparator<Product> {

        @Override
        public int compare(Product o1, Product o2) {
            if (sortOrder.equals(SortOrder.asc)) {
                return o1.getPrice().compareTo(o2.getPrice());
            }
            return o2.getPrice().compareTo(o1.getPrice());
        }
    }

    private record DescriptionComparator(SortOrder sortOrder) implements Comparator<Product> {

        @Override
        public int compare(Product o1, Product o2) {
            if (sortOrder.equals(SortOrder.asc)) {
                return o1.getDescription().compareTo(o2.getDescription());
            }
            return o2.getDescription().compareTo(o1.getDescription());
        }
    }

    private record RelevanceComparator(String[] queries) implements Comparator<Product> {
        @Override
        public int compare(Product o1, Product o2) {
            return instance.countNumOfMatchingSubstrings(o2.getDescription(), queries)
                    - instance().countNumOfMatchingSubstrings(o1.getDescription(), queries);
        }
    }
}
