package com.example.mffhomedelivery.Callback;

import java.util.List;

import Model.BestDeals;

public interface BestDealsCallbackListener {
    void onBestDealLoadSuccess(List<BestDeals> bestDealModels);
    void onBestDealLoadFailed(String message);
}
