package com.es.phoneshop.model.product.viewed;

import com.es.phoneshop.dto.ViewedProductDto;
import com.es.phoneshop.mapper.ProductMapper;
import com.es.phoneshop.mapper.impl.ProductMapperImpl;
import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ViewedProductsServiceTest {
    private List<ViewedProductDto> viewedProducts;
    private String ATTRIBUTE_NAME = "viewed";
    private HttpServletRequest request = mock(HttpServletRequest.class);
    private HttpSession session = mock(HttpSession.class);
    private Currency currency = Currency.getInstance("USD");
    private ViewedProductsService viewedProductsService;
    private ProductMapper productMapper = new ProductMapperImpl();
    private Product testProduct;

    @Before
    public void setup() {
        viewedProducts = new ArrayList<>();
        viewedProducts.add(new ViewedProductDto(1L, "a", "1", currency, new BigDecimal(100)));
        viewedProducts.add(new ViewedProductDto(2L, "b", "2", currency, new BigDecimal(100)));
        viewedProducts.add(new ViewedProductDto(3L, "c", "3", currency, new BigDecimal(100)));
        viewedProducts.add(new ViewedProductDto(4L, "d", "4", currency, new BigDecimal(100)));
        viewedProductsService = ViewedProductsServiceImpl.INSTANCE;
        testProduct = new Product(5L, "e", "e", new BigDecimal(100), currency, 10, "x");
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void ifViewedProductsAttributeIsNotEmptyThenReturnThem() {
        when(request.getSession().getAttribute(ATTRIBUTE_NAME)).thenReturn(viewedProducts);

        List<ViewedProductDto> result = viewedProductsService.getViewedProducts(request);
        assertNotNull(result);
    }

    @Test
    public void ifViewedProductsAttributeIsEmptyThenReturnEmpty() {
        when(request.getSession().getAttribute(ATTRIBUTE_NAME)).thenReturn(null);

        List<ViewedProductDto> result = viewedProductsService.getViewedProducts(request);
        assertTrue(result.isEmpty());
    }

    @Test
    public void ifAddProductThenReturnUpdatedListWithThreeElementsAndWithoutRepeatedElements() {
        List<ViewedProductDto> expected = viewedProducts;
        expected.add(0, productMapper.toRecentlyViewedProduct(testProduct));
        expected = expected.stream().limit(3).collect(Collectors.toList());

        List<ViewedProductDto> result = viewedProductsService.addViewedProduct(viewedProducts, testProduct);

        assertEquals(expected, result);
    }


}
