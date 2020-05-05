package com.example.mffhomedelivery.Callback;

import java.util.List;

import Model.PopularCategories;

public interface PopularCategoriesCallbackListener {
    void onPopularLoadSuccess(List<PopularCategories> popularCategories);
    void onPopularLoadFailed(String message);
}
