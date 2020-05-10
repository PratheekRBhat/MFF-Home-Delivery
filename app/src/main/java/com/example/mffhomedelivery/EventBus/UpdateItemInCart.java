package com.example.mffhomedelivery.EventBus;

import com.example.mffhomedelivery.Database.CartItem;

public class UpdateItemInCart {
    public CartItem cartItem;

    public UpdateItemInCart(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }
}
