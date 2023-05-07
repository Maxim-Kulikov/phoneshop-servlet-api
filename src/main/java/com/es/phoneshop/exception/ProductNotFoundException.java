package com.es.phoneshop.exception;

public class ProductNotFoundException extends RuntimeException{
    private String productId;

    public ProductNotFoundException(String productId) {
        this.productId = productId;
    }

    public String getProductId(){
        return this.productId;
    }
}
