package com.example.mffhomedelivery.ui.cart;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mffhomedelivery.Adapter.CartAdapter;
import com.example.mffhomedelivery.Common.Common;
import com.example.mffhomedelivery.Database.CartDataSource;
import com.example.mffhomedelivery.Database.CartDatabase;
import com.example.mffhomedelivery.Database.LocalCartDataSource;
import com.example.mffhomedelivery.EventBus.HideFABCart;
import com.example.mffhomedelivery.EventBus.UpdateItemInCart;
import com.example.mffhomedelivery.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CartFragment extends Fragment {
    Parcelable recyclerViewState;
    Unbinder unbinder;
    private CartDataSource cartDataSource;
    private CartAdapter adapter;

    @BindView(R.id.recycler_cart)
    RecyclerView cartRV;
    @Nullable
    @BindView(R.id.txt_cart_food_price)
    TextView totalPriceTV;
    @BindView(R.id.group_place_holder)
    CardView groupPlaceHolderCV;
    @BindView(R.id.txt_empty_cart)
    TextView emptyCartTV;

    private CartViewModel cartViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        cartViewModel = ViewModelProviders.of(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        unbinder = ButterKnife.bind(this, root);
        cartViewModel.initCartDataSource(getContext());
        cartViewModel.getMutableLiveDataCartItemList().observe(getViewLifecycleOwner(), cartItems -> {
            if(cartItems == null || cartItems.isEmpty()){
                cartRV.setVisibility(View.GONE);
                groupPlaceHolderCV.setVisibility(View.GONE);
                emptyCartTV.setVisibility(View.VISIBLE);

            } else{
                cartRV.setVisibility(View.VISIBLE);
                groupPlaceHolderCV.setVisibility(View.VISIBLE);
                emptyCartTV.setVisibility(View.GONE);

                adapter = new CartAdapter(getContext(),cartItems);
                cartRV.setAdapter(adapter);

            }
        });
        initViews();

        return root;
    }

    private void initViews() {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).CartDao());

        EventBus.getDefault().postSticky(new HideFABCart(true));

        cartRV.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        cartRV.setLayoutManager(layoutManager);
        cartRV.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().postSticky(new HideFABCart(false));
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUpdateItemInCartEvent(UpdateItemInCart event){
        if (event.getCartItem() != null) {
            //Saving state of Recycler View
            recyclerViewState = Objects.requireNonNull(cartRV.getLayoutManager()).onSaveInstanceState();
            cartDataSource.updateCartItems(event.getCartItem())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            calculateTotalPrice();
                            cartRV.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void calculateTotalPrice() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double price) {
                        totalPriceTV.setText(new StringBuilder("Total: \u20B9")
                                .append(Common.formatPrice(price)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), "[SUM CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
