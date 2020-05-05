package com.example.mffhomedelivery.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mffhomedelivery.Callback.BestDealsCallbackListener;
import com.example.mffhomedelivery.Callback.PopularCategoriesCallbackListener;
import com.example.mffhomedelivery.Common.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Model.BestDeals;
import Model.PopularCategories;

        public class HomeViewModel extends ViewModel implements PopularCategoriesCallbackListener, BestDealsCallbackListener {
    private MutableLiveData<List<PopularCategories>> popularList;
    private MutableLiveData<List<BestDeals>> bestDealList;
    private MutableLiveData<String> messageError;
    private PopularCategoriesCallbackListener popularCallbackListener;
    private BestDealsCallbackListener bestDealsCallbackListener;

    //Method to initialise Callback Listeners.
    public HomeViewModel() {
        popularCallbackListener = this;
        bestDealsCallbackListener = this;
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
        final List<PopularCategories> tempList = new ArrayList<>();     //Create a list to store the popular categories items.
        DatabaseReference popularRef = FirebaseDatabase.getInstance().getReference(Common.POPULAR_CATEGORIES_REF);  //firebase database reference.
        popularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapShot: dataSnapshot.getChildren())
                {
                    PopularCategories model = itemSnapShot.getValue(PopularCategories.class);       //add items.
                    tempList.add(model);
                }
                popularCallbackListener.onPopularLoadSuccess(tempList);                             //success message.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                popularCallbackListener.onPopularLoadFailed(databaseError.getMessage());            //failure message.
            }
        });
    }

    //Same as popular categories version.
    public MutableLiveData<List<BestDeals>> getBestDealList() {
        if(bestDealList == null)
        {
            bestDealList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadBestDealList();
        }
        return bestDealList;
    }

    //same as popular categories version.
    private void loadBestDealList() {
        final List<BestDeals> tempList = new ArrayList<>();
        DatabaseReference bestDealRef = FirebaseDatabase.getInstance().getReference(Common.BEST_DEALS_REF);
        bestDealRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapShot: dataSnapshot.getChildren())
                {
                    tempList.add(itemSnapShot.getValue(BestDeals.class));
                }
                bestDealsCallbackListener.onBestDealLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                bestDealsCallbackListener.onBestDealLoadFailed(databaseError.getMessage());
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

    @Override
    public void onBestDealLoadSuccess(List<BestDeals> bestDealModels) {
        bestDealList.setValue(bestDealModels);
    }

    @Override
    public void onBestDealLoadFailed(String message) {
        messageError.setValue(message);
    }
}