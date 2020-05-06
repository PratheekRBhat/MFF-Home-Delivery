package com.example.mffhomedelivery.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements CartDataSource{
    private CartDao cartDao;

    public LocalCartDataSource(CartDao cartDao) {
        this.cartDao = cartDao;
    }

    @Override
    public Flowable<List<CartItem>> getAllCartItems(String uid) {
        return cartDao.getAllCartItems(uid);
    }

    @Override
    public Single<Integer> countItemsInCart(String uid) {
        return cartDao.countItemsInCart(uid);
    }

    @Override
    public Single<Long> sumPriceInCart(String uid) {
        return cartDao.sumPriceInCart(uid);
    }

    @Override
    public Single<CartItem> getItemInCart(String foodID, String uid) {
        return cartDao.getItemInCart(foodID, uid);
    }

    @Override
    public Completable insertOrReplaceAll(CartItem... cartItems) {
        return cartDao.insertOrReplaceAll(cartItems);
    }

    @Override
    public Single<Integer> updateCartItems(CartItem cartItem) {
        return cartDao.updateCartItems(cartItem);
    }

    @Override
    public Single<Integer> deleteCartItems(CartItem cartItem) {
        return cartDao.deleteCartItems(cartItem);
    }

    @Override
    public Single<Integer> cleanCart(String uid) {
        return cartDao.cleanCart(uid);
    }
}
