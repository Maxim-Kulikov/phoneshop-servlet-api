package com.es.phoneshop.service;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.repository.ProductDao;
import com.es.phoneshop.service.impl.DefaultCartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCartServiceTest {
    @Mock
    private ProductDao productDao;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @InjectMocks
    private CartService cartService = DefaultCartService.INSTANCE;
    private Currency currency = Currency.getInstance("USD");
    private List<Product> testProducts;
    private Cart cart;


    @Before
    public void init() {
        testProducts = new ArrayList<>();
        initProducts();
        cart = new Cart();
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void ifGetCartAndCartAttributeIsExistedThenReturnCart() {
        when(request.getSession().getAttribute(any())).thenReturn(cart);
        assertEquals(cart, cartService.getCart(request));
    }

    @Test
    public void ifGetCartAndCartAttributeIsNotExistedThenReturnNewCart() {
        when(request.getSession().getAttribute(any())).thenReturn(null);
        assertNotNull(cartService.getCart(request));
    }

    @Test
    public void ifAddInCartThenReturnCartWithAddedProduct() throws OutOfStockException {
        Product testProduct = testProducts.get(0);

        when(productDao.get(anyLong())).thenReturn(testProduct);

        Cart expected = new Cart();
        Cart result = new Cart();

        expected.getItems().add(new CartItem(testProduct, 5));
        expected.setTotalQuantity(5);
        expected.setTotalCost(new BigDecimal(500));
        cartService.add(result, anyLong(), 5);

        assertEquals(expected, result);
    }

    @Test(expected = OutOfStockException.class)
    public void ifProductQuantityMoreThenStockThenThrowException() throws OutOfStockException {
        Cart cart = this.cart;
        Product productToAdd = testProducts.get(0);

        when(productDao.get(anyLong())).thenReturn(productToAdd);

        cart.getItems().add(new CartItem(productToAdd, 5));
        cartService.add(cart, productToAdd.getId(), 6);
    }

    @Test
    public void ifProductWasExistedBeforeThenIncreaseQuantity() throws OutOfStockException {
        Cart cart = this.cart;
        Product productToAdd = testProducts.get(0);

        when(productDao.get(anyLong())).thenReturn(productToAdd);

        CartItem cartItem = new CartItem(productToAdd, 5);
        cart.getItems().add(cartItem);
        cartService.add(cart, productToAdd.getId(), 4);

        assertEquals(9, cartItem.getQuantity());
    }

    @Test
    public void checkIfProductsHaveUpdatedQuantityAfterUpdating() throws OutOfStockException {
        Product testProduct = testProducts.get(0);
        when(productDao.get(anyLong())).thenReturn(testProduct);
        cartService.add(cart, testProduct.getId(), 5);
        cartService.update(cart, testProduct.getId(), 7);
        assertEquals(7, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void checkDeletingOfProduct() {
        Cart cart = new Cart();
        cart.getItems().add(new CartItem(testProducts.get(0), 2));
        cart.getItems().add(new CartItem(testProducts.get(1), 3));

        cartService.delete(cart, 1L);
        assertFalse(cart.getItems().contains(testProducts.get(0)));
    }

    @Test
    public void checkDeletingOfAllProducts() {
        Cart cart = new Cart();
        cart.getItems().add(new CartItem(testProducts.get(0), 2));
        cart.getItems().add(new CartItem(testProducts.get(1), 3));

        cartService.clear(cart);
        assertTrue(cart.getItems().isEmpty());
        assertEquals(new BigDecimal(0), cart.getTotalCost());
        assertEquals(0, cart.getTotalQuantity());
    }

    private void initProducts() {
        testProducts.add(new Product(1L, "e", "e", new BigDecimal(100), currency, 10, "x"));
        testProducts.add(new Product(2L, "k", "b", new BigDecimal(100), currency, 10, "x"));
        testProducts.add(new Product(3L, "f", "n", new BigDecimal(100), currency, 10, "x"));
        testProducts.add(new Product(4L, "p", "b", new BigDecimal(100), currency, 10, "x"));
    }

}
