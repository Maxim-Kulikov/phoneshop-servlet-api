package com.es.phoneshop.model.order;

import com.es.phoneshop.model.IdOwner;
import com.es.phoneshop.model.cart.Cart;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

@Getter
@Setter
public class Order extends Cart implements IdOwner {
    private Long id;
    private String secureId;
    private BigDecimal deliveryCost;
    private BigDecimal subtotal;
    private Currency currency;
    private String firstName;
    private String lastName;
    private String phone;
    private Date deliveryDate;
    private String deliveryAddress;
    private PaymentMethod paymentMethod;
}
