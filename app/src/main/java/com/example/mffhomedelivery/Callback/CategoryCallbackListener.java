package com.example.mffhomedelivery.Callback;

import java.util.List;

import Model.Category;

public interface CategoryCallbackListener {
    void onCategoryLoadSuccess(List<Category> categoryList);
    void onCategoryLoadFailed(String message);
}
