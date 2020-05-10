package com.example.mffhomedelivery.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {
    Flowable<List<CartItem>> getAllCartItems(String uid);

    Single<Integer> countItemsInCart(String uid);

    Single<Double> sumPriceInCart(String uid);

    Single<CartItem> getItemInCart(String foodID, String uid);

    Completable insertOrReplaceAll(CartItem... cartItems);

    Single<Integer> updateCartItems(CartItem cartItem);

    Single<Integer> deleteCartItems(CartItem cartItem);

    Single<Integer> cleanCart(String uid);
}
