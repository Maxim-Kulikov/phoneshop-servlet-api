package com.es.phoneshop.model.product.cart;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    private Currency currency = Currency.getInstance("USD");
    private List<Product> testProducts;
    @InjectMocks
    private CartService cartService = DefaultCartService.INSTANCE;
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

        when(productDao.getProduct(anyLong())).thenReturn(testProduct);

        Cart expected = new Cart();
        Cart result = new Cart();

        expected.add(new CartItem(testProduct, 5));
        cartService.add(result, anyLong(), 5);

        assertEquals(expected, result);
    }

    @Test(expected = OutOfStockException.class)
    public void ifProductQuantityMoreThenStockThenThrowException() throws OutOfStockException {
        Cart cart = this.cart;
        Product productToAdd = testProducts.get(0);

        when(productDao.getProduct(anyLong())).thenReturn(productToAdd);

        cart.add(new CartItem(productToAdd, 5));
        cartService.add(cart, productToAdd.getId(), 6);
    }

    @Test
    public void ifProductWasExistedBeforeThenIncreaseQuantity() throws OutOfStockException {
        Cart cart = this.cart;
        Product productToAdd = testProducts.get(0);

        when(productDao.getProduct(anyLong())).thenReturn(productToAdd);

        CartItem cartItem = new CartItem(productToAdd, 5);
        cart.add(cartItem);
        cartService.add(cart, productToAdd.getId(), 4);

        assertEquals(9, cartItem.getQuantity());
    }

    private void initProducts() {
        testProducts.add(new Product(1L, "e", "e", new BigDecimal(100), currency, 10, "x"));
        testProducts.add(new Product(2L, "k", "b", new BigDecimal(100), currency, 10, "x"));
        testProducts.add(new Product(3L, "f", "n", new BigDecimal(100), currency, 10, "x"));
        testProducts.add(new Product(4L, "p", "b", new BigDecimal(100), currency, 10, "x"));
    }
}
