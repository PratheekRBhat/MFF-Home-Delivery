package com.example.mffhomedelivery.Callback;

import Model.Order;

public interface LoadTimeFromFirebaseListener {
    void onLoadTimeSuccess(Order order, long estimateTimeInMs);
    void onLoadTimeFailed(String message);
}
