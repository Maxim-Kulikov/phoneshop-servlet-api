package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;
import org.codehaus.plexus.util.StringUtils;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ArrayListProductDao implements ProductDao {
    INSTANCE;

    private Long productId;
    private List<Product> products;
    private final Lock readLock;
    private final Lock writeLock;
    private final Comparator<Product> ascPriceComparator;
    private final Comparator<Product> descPriceComparator;
    private final Comparator<Product> ascDescriptionComparator;
    private final Comparator<Product> descDescriptionComparator;

    ArrayListProductDao() {
        this.productId = 1L;
        products = new ArrayList<>();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        readLock = readWriteLock.readLock();
        writeLock = readWriteLock.writeLock();
        ascPriceComparator = new PriceComparator(SortOrder.asc);
        descPriceComparator = new PriceComparator(SortOrder.desc);
        ascDescriptionComparator = new DescriptionComparator(SortOrder.asc);
        descDescriptionComparator = new DescriptionComparator(SortOrder.desc);
    }

    @Override
    public Product getProduct(Long id) throws ProductNotFoundException {
        try {
            readLock.lock();
            return getOptionalOfProduct(id)
                    .orElseThrow(() -> new ProductNotFoundException(id.toString()));
        } finally {
            readLock.unlock();
        }
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

    @Override
    public void save(Product product) {
        if (product == null) {
            throw new RuntimeException("Product is null");
        }

        try {
            writeLock.lock();

            if (product.getId() == null) {
                saveProduct(product);
                return;
            }
            Optional<Product> optional = getOptionalOfProduct(product.getId());
            if (optional.isEmpty()) {
                saveProduct(product);
            } else {
                changeProduct(optional.get(), product);
            }
        } finally {
            writeLock.unlock();
        }
    }

    private void saveProduct(Product product) {
        product.setId(productId++);
        products.add(product);
    }

    /**
     * this method uses inner class and replaces old values of existed product with new
     */
    private void changeProduct(Product existed, Product product) {
        existed.changer()
                .code(product.getCode())
                .imageUrl(product.getImageUrl())
                .currency(product.getCurrency())
                .price(product.getPrice())
                .description(product.getDescription())
                .stock(product.getStock());
    }

    /**
     * return optional of product by id for next checking of existence
     */
    private Optional<Product> getOptionalOfProduct(Long id) {
        return products.stream()
                .filter(product -> id.equals(product.getId()))
                .findAny();
    }

    @Override
    public void delete(Long id) {
        try {
            writeLock.lock();
            Optional<Product> optional = getOptionalOfProduct(id);

            if (optional.isEmpty()) {
                throw new ProductNotFoundException(id.toString());
            }
            products.remove(optional.get());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void deleteAll() {
        productId = 1L;
        products.clear();
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
            return INSTANCE.countNumOfMatchingSubstrings(o2.getDescription(), queries)
                    - INSTANCE.countNumOfMatchingSubstrings(o1.getDescription(), queries);
        }
    }
}
