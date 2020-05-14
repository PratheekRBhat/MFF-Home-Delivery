package com.example.mffhomedelivery.Callback;

import java.util.List;

import Model.Order;

public interface LoadOrderCallbackListener {
    void onLoadOrderSuccess(List<Order> orderList);
    void onLoadOrderFailed(String message);
}
