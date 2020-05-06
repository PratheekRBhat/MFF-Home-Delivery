package com.example.mffhomedelivery.ui.foodlist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mffhomedelivery.Common.Common;

import java.util.List;

import Model.Food;

public class FoodListViewModel extends ViewModel {

    private MutableLiveData<List<Food>> mutableLiveDataFoodList;

    public FoodListViewModel() {
    }

    public MutableLiveData<List<Food>> getMutableLiveDataFoodList() {
        if (mutableLiveDataFoodList == null){
            mutableLiveDataFoodList = new MutableLiveData<>();
        }
        mutableLiveDataFoodList.setValue(Common.categorySelected.getFoods());
        return mutableLiveDataFoodList;
    }
}