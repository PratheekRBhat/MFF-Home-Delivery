package com.example.mffhomedelivery.ui.cart;

import android.app.AlertDialog;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.example.mffhomedelivery.Callback.LoadTimeFromFirebaseListener;
import com.example.mffhomedelivery.Common.Common;
import com.example.mffhomedelivery.Common.SwipeHelper;
import com.example.mffhomedelivery.Database.CartDataSource;
import com.example.mffhomedelivery.Database.CartDatabase;
import com.example.mffhomedelivery.Database.CartItem;
import com.example.mffhomedelivery.Database.LocalCartDataSource;
import com.example.mffhomedelivery.EventBus.CounterCartEvent;
import com.example.mffhomedelivery.EventBus.HideFABCart;
import com.example.mffhomedelivery.EventBus.MenuItemBack;
import com.example.mffhomedelivery.EventBus.UpdateItemInCart;
import com.example.mffhomedelivery.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import Model.Order;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CartFragment extends Fragment implements LoadTimeFromFirebaseListener {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Parcelable recyclerViewState;
    Unbinder unbinder;
    private CartDataSource cartDataSource;
    private CartAdapter adapter;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;

    LoadTimeFromFirebaseListener listener;

    @BindView(R.id.recycler_cart)
    RecyclerView cartRV;
    @BindView(R.id.txt_total_price)
    TextView totalPriceTV;
    @BindView(R.id.group_place_holder)
    CardView groupPlaceHolderCV;
    @BindView(R.id.txt_empty_cart)
    TextView emptyCartTV;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        CartViewModel cartViewModel = ViewModelProviders.of(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        unbinder = ButterKnife.bind(this, root);

        listener = this;

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
        initLocation();

        return root;
    }

    @OnClick(R.id.btn_place_order)
    void onPlaceOrderCLick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Order Details");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_place_order, null);

        EditText addressET = view.findViewById(R.id.edt_address);
        EditText commentET = view.findViewById(R.id.edt_comment);

        TextView addressDetailsTV = view.findViewById(R.id.txt_address_detail);
        TextView enableLocationTV = view.findViewById(R.id.txt_enable_location);

        RadioButton homeRB = view.findViewById(R.id.rdi_home_address);
        RadioButton currentRB = view.findViewById(R.id.rdi_current_address);
        RadioButton codRB = view.findViewById(R.id.rdi_cod);

        //Data
        addressET.setText(Common.currentUser.getAddress());

        //Event
        homeRB.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
            {
                addressET.setText(Common.currentUser.getAddress());
                addressDetailsTV.setVisibility(View.GONE);
                enableLocationTV.setVisibility(View.GONE);
            }
        });
        currentRB.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
            {
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                String coordinates = new StringBuilder()
                                        .append(location.getLatitude())
                                        .append(" / ")
                                        .append(location.getLongitude()).toString();

                                Single<String> singleAddress = Single.just(getAddressFromLatLng(location.getLatitude(), location.getLongitude()));

                                Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        addressET.setText(coordinates);
                                        addressDetailsTV.setText(s);
                                        addressDetailsTV.setVisibility(View.VISIBLE);
                                        enableLocationTV.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        addressET.setText(coordinates);
                                        addressDetailsTV.setText(R.string.need_billing_address);
                                        addressDetailsTV.setVisibility(View.VISIBLE);
                                        enableLocationTV.setVisibility(View.GONE);
                                    }
                                });
                            } else{
                                enableLocationTV.setVisibility(View.VISIBLE);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            addressDetailsTV.setVisibility(View.GONE);
                            enableLocationTV.setVisibility(View.GONE);
                        });
            }
        });

        builder.setView(view);
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        }).setPositiveButton("Proceed", (dialogInterface, i) -> {
            if (codRB.isChecked())
                paymentCOD(addressET.getText().toString(), commentET.getText().toString());
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void paymentCOD(String address, String comment) {
        compositeDisposable.add(cartDataSource.getAllCartItems(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    //When all cart items are retrieved, the total price can also be retrieved.
                    cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Double>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onSuccess(Double totalPrice) {
                                    double finalPrice = totalPrice;
                                    Order order = new Order();
                                    order.setUserId(Common.currentUser.getUid());
                                    order.setUserName(Common.currentUser.getName());
                                    order.setUserPhone(Common.currentUser.getPhone());
                                    order.setShippingAddress(Common.currentUser.getAddress());
                                    order.setComment(comment);

                                    if (currentLocation != null) {
                                        order.setLat(currentLocation.getLatitude());
                                        order.setLng(currentLocation.getLongitude());
                                    } else {
                                        order.setLat(-0.1f);
                                        order.setLng(-0.1f);
                                    }

                                    order.setCartItemList(cartItems);
                                    order.setTotalPayment(totalPrice);
                                    order.setFinalPayment(finalPrice);
                                    order.setCod(true);
                                    order.setTransactionID("Cash On Delivery");

                                    //Sync device time and date with global time and date. Then submit the order to Firebase database.
                                    syncLocalTimeWithGlobalTime(order);
                                }

                                @Override
                                public void onError(Throwable e) {
//                                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }, throwable -> {
                    Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private void syncLocalTimeWithGlobalTime(Order order) {
        final DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long offset = dataSnapshot.getValue(Long.class);
                long estimateServerTimeMs = System.currentTimeMillis() + offset;    //offset is missing time between your local time and server time

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy HH:mm");
                Date resultDate = new Date(estimateServerTimeMs);

                Log.d("TEST_DATE", ""+sdf.format(resultDate));
                //When the sync succeeds, submit the order to Firebase database.
                listener.onLoadTimeSuccess(order, estimateServerTimeMs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onLoadTimeFailed(databaseError.getMessage());
            }
        });
    }

    private void writeOrderToFirebase(Order order) {
        FirebaseDatabase.getInstance()
                .getReference(Common.ORDER_REF)
                .child(Common.createOrderNumber())
                .setValue(order)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> {
                    cartDataSource.cleanCart(Common.currentUser.getUid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Integer>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onSuccess(Integer integer) {
                                    Toast.makeText(getContext(), "Order placed successfully !", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                });
    }

    //Method to convert location co-ordinates from LatLng to String.
    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String result = "";
        try{
            List<Address> addressList = geocoder.getFromLocation(latitude,longitude, 1);
            if(addressList != null && addressList.size() > 0)
            {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder(address.getAddressLine(0));
                result = sb.toString();
            }
            else
                result = "Address not found";
        } catch (IOException e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    //Method to initialise Location related objects.
    private void initLocation() {
        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    //Method to get Location Callback.
    private void buildLocationCallback() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
    }

    //Method to generate Location request.
    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    //Method to initialise views and adapters and other components.
    private void initViews() {
        setHasOptionsMenu(true);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).CartDao());

        EventBus.getDefault().postSticky(new HideFABCart(true));

        cartRV.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        cartRV.setLayoutManager(layoutManager);
        cartRV.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        SwipeHelper swipeHelper = new SwipeHelper(getContext(), cartRV, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Delete", 30, 0, Color.parseColor("#FF3C30"),
                        pos -> {
                            CartItem cartItem = adapter.getItemAtPositon(pos);
                            cartDataSource.deleteCartItems(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                            adapter.notifyItemRemoved(pos);
                                            sumAllItemInCart();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true)); //update FAB
                                            Toast.makeText(getContext(), "Item deleted from cart successfully", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }));
            }
        };

        sumAllItemInCart();
    }

    private void sumAllItemInCart() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double aDouble) {
                        totalPriceTV.setText(new StringBuilder("Total: \u20B9").append(aDouble));
                    }

                    @Override
                    public void onError(Throwable e) {
//                        if(!e.getMessage().contains("Query returned empty"))
//                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);    //False to hide already inflated home menu.
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.cart_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_clear_cart) {
            cartDataSource.cleanCart(Common.currentUser.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            Toast.makeText(getContext(), "Cart cleared successfully", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
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
//                        Toast.makeText(getContext(), "[SUM CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onLoadTimeSuccess(Order order, long estimateTimeInMs) {
        order.setCreateDate(estimateTimeInMs);
        order.setOrderStatus(0);
        //Submit the order to Firebase database.
        writeOrderToFirebase(order);
    }

    @Override
    public void onLoadTimeFailed(String message) {
        Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();
    }
}
