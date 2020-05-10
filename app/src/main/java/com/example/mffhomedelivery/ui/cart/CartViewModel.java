package com.example.mffhomedelivery.ui.cart;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mffhomedelivery.Common.Common;
import com.example.mffhomedelivery.Database.CartDataSource;
import com.example.mffhomedelivery.Database.CartDatabase;
import com.example.mffhomedelivery.Database.CartItem;
import com.example.mffhomedelivery.Database.LocalCartDataSource;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CartViewModel extends ViewModel {
    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;
    private MutableLiveData<List<CartItem>> mutableLiveDataCartItems;

    public CartViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void onStop(){
        compositeDisposable.clear();
    }

    public void initCartDataSource(Context context){
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).CartDao());
    }


    public MutableLiveData<List<CartItem>> getMutableLiveDataCartItemList() {
        if (mutableLiveDataCartItems == null){
            mutableLiveDataCartItems = new MutableLiveData<List<CartItem>>();
        }
        getAllCartItems();
        return mutableLiveDataCartItems;
    }

    private void getAllCartItems() {
        compositeDisposable.add(cartDataSource.getAllCartItems(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> mutableLiveDataCartItems.setValue(cartItems), throwable -> {
                    mutableLiveDataCartItems.setValue(null);
                }));

    }
}
