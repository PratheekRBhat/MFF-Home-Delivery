package com.example.mffhomedelivery.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CartDao {
    @Query("SELECT * FROM Cart WHERE uid=:uid")
    Flowable<List<CartItem>> getAllCartItems(String uid);

    @Query("SELECT COUNT(*) FROM Cart WHERE uid=:uid")
    Single<Integer> countItemsInCart(String uid);

    @Query("SELECT SUM(foodPrice*foodQuantity) FROM Cart WHERE uid=:uid")
    Single<Long> sumPriceInCart(String uid);

    @Query("SELECT * FROM Cart WHERE foodID=:foodID AND uid=:uid")
    Single<CartItem> getItemInCart(String foodID, String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(CartItem... cartItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCartItems(CartItem cartItem);

    @Delete
    Single<Integer> deleteCartItems(CartItem cartItem);

    @Query("DELETE * FROM Cart WHERE uid=:uid")
    Single<Integer> cleanCart(String uid);
}
