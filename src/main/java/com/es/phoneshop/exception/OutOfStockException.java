package com.es.phoneshop.exception;

import com.es.phoneshop.model.product.Product;
import lombok.Getter;

@Getter
public class OutOfStockException extends Exception {
    private Product product;
    private int stockRequested;
    private int stockAvailable;

    public OutOfStockException(Product product, int stockRequested, int stockAvailable) {
        this.product = product;
        this.stockAvailable = stockAvailable;
        this.stockRequested = stockRequested;
    }
}
