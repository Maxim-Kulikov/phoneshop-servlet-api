package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;
import com.es.phoneshop.repository.impl.ArrayListProductDao;
import com.es.phoneshop.repository.ProductDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest {
    private Product productToSave;
    private Currency usd;
    private List<Product> sampleProducts;
    private ProductDao productDao;

    @Before
    public void initProductDaoProducts() {
        productToSave = new Product("test", "test", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        sampleProducts = new ArrayList<>();
        usd = Currency.getInstance("USD");
        sampleProducts.add(new Product("slls", "new", new BigDecimal(70), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
        sampleProducts.add(new Product("mxmv", "kdk", new BigDecimal(60), usd, -3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
        sampleProducts.add(new Product("oddl", "oowow", new BigDecimal(120), usd, 7, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
        sampleProducts.add(new Product("oeppe", "kkel", new BigDecimal(40), usd, 9, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
        productDao = ArrayListProductDao.instance();
        initProductDaoProducts(productDao);
    }

    @After
    public void clearProductDaoProducts() {
        productDao.deleteAll();
    }

    @Test(expected = ProductNotFoundException.class)
    public void whenFindProductByIncorrectIdThenThrowException() {
        productDao.get(0L);
    }

    @Test
    public void whenFindProductByCorrectIdThenReturnProduct() {
        Product result = productDao.get(1L);
        assertNotNull(result);
    }

    @Test
    public void whenFindProductsThenReturnProductsWithNotNullPriceAndPositiveStock() {
        for (Product product : productDao.findProductsByNameAndSort(null, null, null)) {
            assertTrue(product.getStock() > 0);
            assertNotNull(product.getPrice());
        }
    }

    @Test(expected = RuntimeException.class)
    public void whenProductToSaveIsNullThenThrowException() {
        productDao.save(null);
    }

    @Test
    public void whenSaveProductThenReturnEqualSavedProductToProductBeforeSaving() {
        productDao.save(productToSave);
        Product result = productDao.get(productToSave.getId());
        assertEquals(result, productToSave);
    }

    @Test
    public void whenSaveProductThenProductIdShouldGetValue() {
        productDao.save(productToSave);
        assertNotNull(productToSave.getId());
    }

    @Test(expected = ProductNotFoundException.class)
    public void whenDeleteProductThenShouldThrowExceptionAfterGettingOfDeletedProduct() {
        productDao.delete(1L);
        productDao.get(1L);
    }

    @Test(expected = ProductNotFoundException.class)
    public void whenDeleteProductWithIncorrectIdThenShouldThrowException() {
        productDao.delete(0L);
    }

    @Test
    public void whenProductToSaveHasNotNullExistedIdThenShouldUpdate() {
        Product updatedProduct = new Product(1L, "slls", "new", new BigDecimal(100), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(updatedProduct);
        assertEquals(productDao.get(1L), updatedProduct);
    }

    @Test
    public void whenFindProductsByQueryThenShouldReturnCorrectList() {
        for (Product product : productDao.findProductsByNameAndSort("o", null, null)) {
            assertTrue(product.getDescription().contains("o"));
        }
    }

    @Test
    public void whenFindProductsByAscPriceThenShouldReturnCorrectList() {
        List<Product> expected = getFilteredList(getAscPriceSortedList());
        List<Product> result = productDao.findProductsByNameAndSort(null, SortField.price, SortOrder.asc);
        assertEquals(result, expected);
    }

    @Test
    public void whenFindProductsByDescPriceThenShouldReturnCorrectList() {
        List<Product> expected = getFilteredList(getAscPriceSortedList());
        Collections.reverse(expected);
        List<Product> result = productDao.findProductsByNameAndSort(null, SortField.price, SortOrder.desc);
        assertEquals(result, expected);
    }

    @Test
    public void whenFindProductsByPriceWithoutSortingThenShouldReturnCorrectList() {
        List<Product> expected = getFilteredList(sampleProducts);
        List<Product> result = productDao.findProductsByNameAndSort(null, SortField.price, null);
        assertEquals(result, expected);
    }

    @Test
    public void whenFindProductsWithoutFieldButWithSortingThenShouldReturnCorrectList() {
        List<Product> expected = getFilteredList(sampleProducts);
        List<Product> result = productDao.findProductsByNameAndSort(null, null, SortOrder.desc);
        assertEquals(result, expected);
    }

    @Test
    public void whenFindProductsByAscDescriptionThenShouldReturnCorrectList() {
        List<Product> expected = getFilteredList(getAscDescriptionSortedList());
        List<Product> result = productDao.findProductsByNameAndSort(null, SortField.description, SortOrder.asc);
        assertEquals(result, expected);
    }

    @Test
    public void whenFindProductsByDescDescriptionThenShouldReturnCorrectList() {
        List<Product> expected = getFilteredList(getAscDescriptionSortedList());
        Collections.reverse(expected);
        List<Product> result = productDao.findProductsByNameAndSort(null, SortField.description, SortOrder.desc);
        assertEquals(result, expected);
    }

    private List<Product> getAscPriceSortedList() {
        return sampleProducts.stream()
                .sorted(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return o1.getPrice().compareTo(o2.getPrice());
                    }
                }).toList();
    }

    private List<Product> getAscDescriptionSortedList() {
        return sampleProducts.stream()
                .sorted(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return o1.getDescription().compareTo(o2.getDescription());
                    }
                }).collect(Collectors.toList());
    }

    private List<Product> getFilteredList(List<Product> products) {
        return products.stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .collect(Collectors.toList());
    }

    private void initProductDaoProducts(ProductDao productDao) {
        for (Product product : sampleProducts) {
            productDao.save(product);
        }
    }
}
