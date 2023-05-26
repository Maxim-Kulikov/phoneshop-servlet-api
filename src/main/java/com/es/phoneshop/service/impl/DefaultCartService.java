package com.es.phoneshop.service.impl;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.repository.ProductDao;
import com.es.phoneshop.repository.impl.ArrayListProductDao;
import com.es.phoneshop.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public enum DefaultCartService implements CartService {
    INSTANCE;

    private static final String CART_SESSION_ATTRIBUTE = "cart";
    private ProductDao productDao;
    private final Lock writeLock;

    DefaultCartService() {
        productDao = ArrayListProductDao.instance();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        writeLock = readWriteLock.writeLock();
    }

    @Override
    public Cart getCart(HttpServletRequest request) {
        Cart cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
        if (cart == null) {
            cart = new Cart();
            request.getSession().setAttribute(CART_SESSION_ATTRIBUTE, cart);
        }
        return cart;
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws OutOfStockException {
        try {
            writeLock.lock();
            Product product = productDao.get(productId);
            StockInfo stockInfo = getStockInfo(cart, product, quantity);
            if (stockInfo.availableQuantity - stockInfo.requiredQuantity >= 0) {
                addToCart(cart, product, quantity);
            } else {
                throw new OutOfStockException(product, stockInfo.requiredQuantity, stockInfo.availableQuantity);
            }
            recalculateCart(cart);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void update(Cart cart, Long productId, int quantity) throws OutOfStockException {
        try {
            writeLock.lock();
            Product product = productDao.get(productId);

            if (product.getStock() >= quantity) {
                getOptionalOfCartItem(cart, productId)
                        .orElseThrow(() -> new ProductNotFoundException(productId.toString()))
                        .setQuantity(quantity);
            } else {
                throw new OutOfStockException(product, quantity, product.getStock());
            }
            recalculateCart(cart);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(Cart cart, Long productId) {
        try {
            writeLock.lock();
            cart.getItems().removeIf(item ->
                    item.getProduct().getId().equals(productId));
            recalculateCart(cart);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear(Cart cart) {
        try {
            writeLock.lock();
            cart.getItems().clear();
            cart.setTotalCost(null);
            cart.setTotalQuantity(0);
        } finally {
            writeLock.unlock();
        }
    }


    private StockInfo getStockInfo(Cart cart, Product product, int quantity) {
        Optional<CartItem> optionalCartItem = getOptionalOfCartItem(cart, product.getId());
        if (optionalCartItem.isEmpty()) {
            return new StockInfo(quantity, product.getStock());
        }
        int requiredQuantity = optionalCartItem.get().getQuantity() + quantity;
        int availableQuantity = product.getStock();
        return new StockInfo(requiredQuantity, availableQuantity);
    }

    private void addToCart(Cart cart, Product product, int quantity) {
        Optional<CartItem> optionalCartItem = getOptionalOfCartItem(cart, product.getId());
        if (optionalCartItem.isEmpty()) {
            cart.getItems().add(0, new CartItem(product, quantity));
        } else {
            int quantityBefore = optionalCartItem.get().getQuantity();
            int actualQuantity = quantityBefore + quantity;
            optionalCartItem.get().setQuantity(actualQuantity);
        }
    }

    private Optional<CartItem> getOptionalOfCartItem(Cart cart, Long productId) {
        return cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findAny();
    }

    private void recalculateCart(Cart cart) {
        int quantity = getTotalQuantity(cart);
        BigDecimal cost = getTotalCost(cart);
        cart.setTotalCost(cost);
        cart.setTotalQuantity(quantity);
    }

    private int getTotalQuantity(Cart cart) {
        return cart.getItems().stream()
                .map(CartItem::getQuantity)
                .mapToInt(q -> q)
                .sum();
    }

    private BigDecimal getTotalCost(Cart cart) {
        return cart.getItems().stream()
                .map(this::multiplyPriceByQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal multiplyPriceByQuantity(CartItem cartItem) {
        return cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }

    @AllArgsConstructor
    @Getter
    private static class StockInfo {
        private int requiredQuantity;
        private int availableQuantity;
    }
}
