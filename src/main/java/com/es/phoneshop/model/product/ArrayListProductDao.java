package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.ProductNotFoundException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ArrayListProductDao implements ProductDao {
    INSTANCE;

    private Long productId;
    private final List<Product> products;
    private final Lock readLock;
    private final Lock writeLock;

    ArrayListProductDao() {
        this.productId = 1L;
        products = new ArrayList<>();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        readLock = readWriteLock.readLock();
        writeLock = readWriteLock.writeLock();

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
            if (query != null && !query.isEmpty()) {
                stream = filterByDescription(stream, query);
            }
            if (sortField == null) {
                sortField = SortField.without;
            }
            if (sortOrder == null) {
                sortOrder = SortOrder.without;
            }
            if (sortField != SortField.without && sortOrder != SortOrder.without) {
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
        String[] queries = query.split(" ");
        if (queries.length == 0) {
            return stream;
        }
        return stream
                .filter(product -> filterDescription(product, queries))
                .sorted(new RelevanceComparator());
    }

    private Stream<Product> sortProducts(Stream<Product> stream, SortField sortField, SortOrder sortOrder) {
        return stream
                .sorted(sortComparator(sortField, sortOrder));
    }

    private Comparator<Product> sortComparator(SortField sortField, SortOrder sortOrder) {
        if (sortField.equals(SortField.without) || sortOrder.equals(SortOrder.without)) {
            return new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    return 0;
                }
            };
        }
        if (sortField.equals(SortField.price)) {
            return new PriceComparator(sortOrder);
        }
        return new DescriptionComparator(sortOrder);
    }

    private boolean filterDescription(Product product, String[] queries) {
        if (queries.length == 0) {
            return true;
        }
        String description = product.getDescription();
        for (String x : queries) {
            if (!description.toLowerCase().contains(x.toLowerCase())) {
                return false;
            }
        }
        return true;
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
                throw new RuntimeException("No such element was found");
            }
            products.remove(optional.get());
        } finally {
            writeLock.unlock();
        }
    }

    private record PriceComparator(SortOrder sortOrder) implements Comparator<Product> {

        @Override
        public int compare(Product o1, Product o2) {
            if (sortOrder.equals(SortOrder.without)) {
                return 0;
            }
            if (sortOrder.equals(SortOrder.asc)) {
                return o1.getPrice().compareTo(o2.getPrice());
            }
            return o2.getPrice().compareTo(o1.getPrice());
        }
    }

    private record DescriptionComparator(SortOrder sortOrder) implements Comparator<Product> {

        @Override
        public int compare(Product o1, Product o2) {
            if (sortOrder.equals(SortOrder.without)) {
                return 0;
            }
            if (sortOrder.equals(SortOrder.asc)) {
                return o1.getDescription().compareTo(o2.getDescription());
            }
            return o2.getDescription().compareTo(o1.getDescription());
        }
    }

    private record RelevanceComparator() implements Comparator<Product> {

        @Override
        public int compare(Product o1, Product o2) {
            return o1.getDescription().split(" ").length - o2.getDescription().split(" ").length;
        }
    }
}
