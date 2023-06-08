package com.es.phoneshop.model.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DescriptionSearchStrategy {
    ALL_WORDS("all words"), ANY_WORD("any word");

    private String strategy;

}
