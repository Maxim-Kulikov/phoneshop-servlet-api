package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.ProductNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest {
    private ProductDao productDao;
    private Product productToSave;
    private Currency usd = Currency.getInstance("USD");

    @Before
    public void setup() {
        productDao = ArrayListProductDao.INSTANCE;
        productToSave = new Product("test", "test", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        init();
    }

    @Test(expected = ProductNotFoundException.class)
    public void whenFindProductByIncorrectIdThenThrowException() {
        productDao.getProduct(0L);
    }

    @Test
    public void whenFindProductByCorrectIdThenReturnProduct() {
        assertNotNull(productDao.getProduct(1L));
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
        Product result = productDao.getProduct(productToSave.getId());
        assertEquals(result, productToSave);
    }

    @Test
    public void whenSaveProductThenProductIdShouldGetValue() {
        productDao.save(productToSave);
        assertNotNull(productToSave.getId());
    }

    @Test(expected = RuntimeException.class)
    public void whenDeleteProductThenShouldThrowExceptionAfterGettingOfDeletedProduct() {
        productDao.delete(1L);
        productDao.getProduct(1L);
    }

    @Test(expected = RuntimeException.class)
    public void whenDeleteProductWithIncorrectIdThenShouldThrowException() {
        productDao.delete(0L);
    }

    @Test
    public void whenProductToSaveHasNotNullExistedIdThenShouldUpdate(){
        Product updatedProduct = new Product(1L,"slls", "new", new BigDecimal(100), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(updatedProduct);
        assertEquals(productDao.getProduct(1L), updatedProduct);
    }

    @Test
    public void whenFindProductsByQueryThenShouldReturnCorrectList(){
        for(Product product: productDao.findProductsByNameAndSort("o", null, null)){
            assertTrue(product.getDescription().contains("o"));
        }
    }

    @Test
    public void whenFindProductsByAscPriceThenShouldReturnCorrectList(){
        List<Product> sortedList = productDao.findProductsByNameAndSort(null, null, null)
                .stream()
                .sorted(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return o1.getPrice().compareTo(o2.getPrice());
                    }
                }).toList();
        assertEquals(productDao.findProductsByNameAndSort(null, SortField.price, SortOrder.asc), sortedList);
    }

    @Test
    public void whenFindProductsByDescPriceThenShouldReturnCorrectList() {
        List<Product> sortedList = productDao.findProductsByNameAndSort(null, null, null)
                .stream()
                .sorted(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return o2.getPrice().compareTo(o1.getPrice());
                    }
                }).toList();
        assertEquals(productDao.findProductsByNameAndSort(null, SortField.price, SortOrder.desc), sortedList);
    }

    @Test
    public void whenFindProductsByPriceWithoutSortingThenShouldReturnCorrectList() {
        List<Product> sortedList = productDao.findProductsByNameAndSort(null, null, null)
                .stream()
                .sorted(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return 0;
                    }
                }).toList();
        assertEquals(productDao.findProductsByNameAndSort(null, SortField.price, SortOrder.without), sortedList);

    }

    @Test
    public void whenFindProductsWithoutFieldButWithSortingThenShouldReturnCorrectList() {
        List<Product> sortedList = productDao.findProductsByNameAndSort(null, null, null)
                .stream()
                .sorted(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return 0;
                    }
                }).toList();
        assertEquals(productDao.findProductsByNameAndSort(null, SortField.without, SortOrder.desc), sortedList);
    }

    @Test
    public void whenFindProductsWithoutFieldAndSortingThenShouldReturnCorrectList() {
        List<Product> sortedList = productDao.findProductsByNameAndSort(null, null, null)
                .stream()
                .sorted(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return 0;
                    }
                }).toList();
        assertEquals(productDao.findProductsByNameAndSort(null, SortField.without, SortOrder.without), sortedList);
    }

    @Test
    public void whenFindProductsByAscDescriptionThenShouldReturnCorrectList() {
        List<Product> sortedList = productDao.findProductsByNameAndSort(null, null, null)
                .stream()
                .sorted(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return o1.getDescription().compareTo(o2.getDescription());
                    }
                }).toList();
        assertEquals(productDao.findProductsByNameAndSort(null, SortField.description, SortOrder.asc), sortedList);
    }

    @Test
    public void whenFindProductsByDescDescriptionThenShouldReturnCorrectList() {
        List<Product> sortedList = productDao.findProductsByNameAndSort(null, null, null)
                .stream()
                .sorted(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return o2.getDescription().compareTo(o1.getDescription());
                    }
                }).toList();
        assertEquals(productDao.findProductsByNameAndSort(null, SortField.description, SortOrder.desc), sortedList);
    }

        private void init(){
        productDao.save(new Product("slls", "new", new BigDecimal(100), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
        productDao.save(new Product("mxmv", "kdk", new BigDecimal(100), usd, -3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
        productDao.save(new Product("oddl", "oowow", new BigDecimal(100), usd, 7, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
        productDao.save(new Product("oeppe", "kkel", new BigDecimal(100), usd, 9, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
    }


}
