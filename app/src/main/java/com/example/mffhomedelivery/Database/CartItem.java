package com.example.mffhomedelivery.Database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Cart", primaryKeys = {"uid", "foodID"})
public class CartItem {
    @NonNull
    @ColumnInfo(name = "foodID")
    private String foodID;

    @ColumnInfo(name = "foodName")
    private String foodName;

    @ColumnInfo(name = "foodImage")
    private String foodImage;

    @ColumnInfo(name = "foodPrice")
    private double foodPrice;

    @ColumnInfo(name = "foodQuantity")
    private int foodQuantity;

    @ColumnInfo(name = "userPhone")
    private String userPhone;

    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @NonNull
    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(@NonNull String foodID) {
        this.foodID = foodID;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
    }

    public int getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(int foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (! (obj instanceof CartItem))
            return false;
        CartItem cartItem = (CartItem)obj;
        return cartItem.getFoodID().equals(this.foodID);
    }
}
