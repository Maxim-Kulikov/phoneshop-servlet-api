package com.es.phoneshop.model.product;

import com.es.phoneshop.model.IdOwner;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@EqualsAndHashCode
public class Product implements Serializable, IdOwner {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String code;
    private String description;
    /**
     * null means there is no price because the product is outdated or new
     */
    private Currency currency;
    private int stock;
    private String imageUrl;
    /**
     * created inner class Changer for comfortable updating of product
     */
    @EqualsAndHashCode.Exclude
    private final Changer changer = new Changer();

    @EqualsAndHashCode.Exclude
    private final List<PriceInfo> priceInfoList = new ArrayList<>();

    public Product() {
    }

    /**
     * constructor for inner initialization
     */
    public Product(Long id, String code, String description, BigDecimal price, Currency currency, int stock, String imageUrl) {
        this.id = id;
        init(code, description, price, currency, stock, imageUrl);
    }

    /**
     * constructor for saving from client
     */
    public Product(String code, String description, BigDecimal price, Currency currency, int stock, String imageUrl) {
        init(code, description, price, currency, stock, imageUrl);
    }

    private void init(String code, String description, BigDecimal price, Currency currency, int stock, String imageUrl) {
        this.code = code;
        this.description = description;
        this.currency = currency;
        this.stock = stock;
        this.imageUrl = imageUrl;
        priceInfoList.add(new PriceInfo(LocalDate.now(), price));
    }

    public List<PriceInfo> getPriceInfoList() {
        return this.priceInfoList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return priceInfoList.get(priceInfoList.size() - 1).getPrice();
    }

    public void setPrice(BigDecimal price) {
        priceInfoList.add(new PriceInfo(LocalDate.now(), price));
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Changer changer() {
        return this.changer;
    }

    /**
     * like builder but changer
     */
    public class Changer {

        private Changer() {
        }

        public Changer id(Long id) {
            if (!Product.this.id.equals(id) && id != null) {
                Product.this.id = id;
            }
            return this;
        }

        public Changer code(String code) {
            if (!Product.this.code.equals(code) && code != null) {
                Product.this.code = code;
            }
            return this;
        }

        public Changer description(String description) {
            if (!Product.this.description.equals(description) && description != null) {
                Product.this.description = description;
            }
            return this;
        }

        public Changer price(BigDecimal price) {
            setPrice(price);
            return this;
        }

        public Changer currency(Currency currency) {
            if (!Product.this.currency.equals(currency) && currency != null) {
                Product.this.currency = currency;
            }
            return this;
        }

        public Changer stock(int stock) {
            if (Product.this.stock != stock) {
                Product.this.stock = stock;
            }
            return this;
        }

        public Changer imageUrl(String imageUrl) {
            if (!Product.this.imageUrl.equals(imageUrl) && imageUrl != null) {
                Product.this.imageUrl = imageUrl;
            }
            return this;
        }

        public Product change() {
            return Product.this;
        }
    }

}