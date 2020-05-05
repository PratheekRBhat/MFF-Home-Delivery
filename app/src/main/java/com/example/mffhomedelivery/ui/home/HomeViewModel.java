package com.example.mffhomedelivery.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mffhomedelivery.Callback.BestDealsCallbackListener;
import com.example.mffhomedelivery.Callback.PopularCategoriesCallbackListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Common.Common;
import Model.BestDeals;
import Model.PopularCategories;

public class HomeViewModel extends ViewModel implements PopularCategoriesCallbackListener {
    private MutableLiveData<List<PopularCategories>> popularList;
    private MutableLiveData<List<BestDeals>> bestDealList;
    private MutableLiveData<String> messageError;
    private PopularCategoriesCallbackListener popularCallbackListener;
    private BestDealsCallbackListener bestDealCallbackListener;

    public HomeViewModel() {
        popularCallbackListener = this;
    }

    public MutableLiveData<List<PopularCategories>> getPopularList() {
        if(popularList == null) //then we will load list from our database.
        {
            popularList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadPopularList(); //function to load list from database
        }
        return popularList;
    }

    private void loadPopularList() {
        final List<PopularCategories> tempList = new ArrayList<>();
        DatabaseReference popularRef = FirebaseDatabase.getInstance().getReference(Common.POPULAR_CATEGORIES_REF);
        popularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapShot: dataSnapshot.getChildren())
                {
                    PopularCategories model = itemSnapShot.getValue(PopularCategories.class);
                    tempList.add(model);
                }
                popularCallbackListener.onPopularLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                popularCallbackListener.onPopularLoadFailed(databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onPopularLoadSuccess(List<PopularCategories> popularCategories) {
        popularList.setValue(popularCategories);
    }

    @Override
    public void onPopularLoadFailed(String message) {
        messageError.setValue(message);
    }
}