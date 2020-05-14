package com.example.mffhomedelivery.ui.orders;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mffhomedelivery.Adapter.OrdersAdapter;
import com.example.mffhomedelivery.Callback.LoadOrderCallbackListener;
import com.example.mffhomedelivery.Common.Common;
import com.example.mffhomedelivery.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Model.Order;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class OrdersFragment extends Fragment implements LoadOrderCallbackListener {

    @BindView(R.id.recycler_orders)
    RecyclerView ordersRV;

    AlertDialog dialog;

    private Unbinder unbinder;

    OrdersViewModel ordersViewModel;

    private LoadOrderCallbackListener listener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ordersViewModel = ViewModelProviders.of(this).get(OrdersViewModel.class);
        View root = inflater.inflate(R.layout.orders_fragment, container, false);
        unbinder = ButterKnife.bind(this,root);

        initViews(root);
        loadOrdersFromFirebase();

        ordersViewModel.getMutableLiveDataOrderList().observe(getViewLifecycleOwner(), orderList -> {
            OrdersAdapter adapter = new OrdersAdapter(getContext(), orderList);
            ordersRV.setAdapter(adapter);
        });

        return root;
    }

    private void loadOrdersFromFirebase() {
        List<Order> orderList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .orderByChild("userId")
                .equalTo(Common.currentUser.getUid())
                .limitToLast(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot orderSnapshot: dataSnapshot.getChildren())
                        {
                            Order order = orderSnapshot.getValue(Order.class);
                            order.setOrderNumber(orderSnapshot.getKey()); //remember to set key
                            orderList.add(order);
                        }
                        listener.onLoadOrderSuccess(orderList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onLoadOrderFailed(databaseError.getMessage());
                    }
                });
    }

    private void initViews(View root) {
        listener = this;
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();

        ordersRV.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ordersRV.setLayoutManager(layoutManager);
        ordersRV.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
    }

    @Override
    public void onLoadOrderSuccess(List<Order> orderList) {
        dialog.dismiss();
        ordersViewModel.setMutableLiveDataOrderList(orderList);
    }

    @Override
    public void onLoadOrderFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
