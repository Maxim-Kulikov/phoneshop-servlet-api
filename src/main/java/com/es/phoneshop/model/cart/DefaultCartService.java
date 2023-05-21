package com.es.phoneshop.model.cart;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public enum DefaultCartService implements CartService {
    INSTANCE;

    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private ProductDao productDao;
    private final Lock readLock;
    private final Lock writeLock;

    DefaultCartService() {
        productDao = ArrayListProductDao.INSTANCE;
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        readLock = readWriteLock.readLock();
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
            Product product = productDao.getProduct(productId);
            StockInfo stockInfo = getStockInfo(cart, product, quantity);
            if (stockInfo.availableQuantity - stockInfo.requiredQuantity >= 0) {
                addToCart(cart, new CartItem(product, quantity));
            } else {
                throw new OutOfStockException(product, stockInfo.requiredQuantity, stockInfo.availableQuantity);
            }
        } finally {
            writeLock.unlock();
        }
    }

    private StockInfo getStockInfo(Cart cart, Product product, int quantity) {
        Optional<CartItem> optionalCartItem = cart.getOptionalOfCartItem(product.getId());
        if (optionalCartItem.isEmpty()) {
            return new StockInfo(quantity, product.getStock());
        }
        int requiredQuantity = optionalCartItem.get().getQuantity() + quantity;
        int availableQuantity = product.getStock();
        return new StockInfo(requiredQuantity, availableQuantity);
    }

    private void addToCart(Cart cart, CartItem cartItem) {
        Long productId = cartItem.getProduct().getId();
        Optional<CartItem> optionalCartItem = cart.getOptionalOfCartItem(productId);
        if (optionalCartItem.isEmpty()) {
            cart.add(cartItem);
        } else {
            optionalCartItem.get().addQuantity(cartItem.getQuantity());
        }
    }

    @AllArgsConstructor
    @Getter
    private static class StockInfo {
        private int requiredQuantity;
        private int availableQuantity;
    }
}
