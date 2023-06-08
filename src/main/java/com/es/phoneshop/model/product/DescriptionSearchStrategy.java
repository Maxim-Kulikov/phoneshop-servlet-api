package com.es.phoneshop.model.product;

import lombok.Getter;

@Getter
public enum DescriptionSearchStrategy {
    ALL_WORDS("all words"), ANY_WORD("any word");

    private final String strategy;

    DescriptionSearchStrategy(String strategy){
        this.strategy = strategy;
    }
}
