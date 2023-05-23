package com.es.phoneshop.repository;

import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.IdOwner;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.repository.impl.ArrayListProductDao;
import com.es.phoneshop.repository.impl.OrderDaoImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class GenericDao<T extends IdOwner> {
    private List<T> list = new ArrayList<>();
    private Long index = 1L;
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();

    public void save(T t){
        if (t == null) {
            throw new RuntimeException(t.getClass().getSimpleName() + " is null");
        }

        try {
            writeLock.lock();

            if (t.getId() == null) {
                saveToList(t);
                return;
            }
            Optional<T> optional = getOptional(t.getId());
            if (optional.isEmpty()) {
                saveToList(t);
            } else {
                if(t.getClass() == Order.class) {
                    updateOrder((Order) optional.get(), (Order) t);
                }
                if(t.getClass() == Product.class){
                    updateProduct((Product) optional.get(), (Product) t);
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    public T get(Long id){
        try {
            readLock.lock();
            return getOptional(id)
                    .orElseThrow(()->getException(id));
        }finally {
            readLock.unlock();
        }
    }

    public void delete(Long id) {
        try {
            writeLock.lock();
            Optional<T> optional = getOptional(id);

            if (optional.isEmpty()) {
                throw getException(id);
            }
            list.remove(optional.get());
        } finally {
            writeLock.unlock();
        }
    }

    public void deleteAll() {
        index = 1L;
        list.clear();
    }

    public List<T> getAll(){
        return list;
    }

    public Long getId(){
        return index;
    }

    private void saveToList(T t) {
        t.setId(index++);
        list.add(t);
    }

    private RuntimeException getException(Long id){
        String name = this.getClass().getName();
        if(name == OrderDaoImpl.class.getSimpleName()){
            return new OrderNotFoundException(id.toString());
        }
        if(name == ArrayListProductDao.class.getSimpleName()){
            return new ProductNotFoundException(id.toString());
        }
        return new ProductNotFoundException(id.toString());
    }
    /**
     * this method uses inner class and replaces old values of existed product with new
     */
    private void updateOrder(Order existed, Order updated) {
        existed.setDeliveryAddress(updated.getDeliveryAddress());
        existed.setPaymentMethod(updated.getPaymentMethod());
        existed.setPhone(updated.getPhone());
        existed.setFirstName(updated.getFirstName());
        existed.setLastName(updated.getLastName());
        existed.setDeliveryDate(updated.getDeliveryDate());
        existed.setDeliveryCost(updated.getDeliveryCost());
        existed.setSubtotal(updated.getSubtotal());
        existed.setCurrency(updated.getCurrency());
    }

    private void updateProduct(Product existed, Product updated) {
        existed.changer()
                .code(updated.getCode())
                .imageUrl(updated.getImageUrl())
                .currency(updated.getCurrency())
                .price(updated.getPrice())
                .description(updated.getDescription())
                .stock(updated.getStock());
    }

    private Optional<T> getOptional(Long id){
        return list.stream()
                .filter(t -> t.getId().equals(id))
                .findAny();
    }
}
