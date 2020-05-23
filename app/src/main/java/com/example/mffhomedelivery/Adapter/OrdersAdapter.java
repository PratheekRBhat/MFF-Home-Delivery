package com.example.mffhomedelivery.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mffhomedelivery.Callback.RecyclerClickListener;
import com.example.mffhomedelivery.Common.Common;
import com.example.mffhomedelivery.Database.CartItem;
import com.example.mffhomedelivery.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Model.Order;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {
    private Context context;
    private List<Order> orderList;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    public OrdersAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        calendar =Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_order_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(orderList.get(position).getCartItemList().get(0).getFoodImage())
                .into(holder.orderIV); //load default image in cart
        calendar.setTimeInMillis(orderList.get(position).getCreateDate());
        Date date = new Date(orderList.get(position).getCreateDate());
        holder.orderDateTV.setText(new StringBuilder(Common.getDateOfWeek(calendar.get(Calendar.DAY_OF_WEEK)))
                .append(" ")
                .append(simpleDateFormat.format(date)));
        holder.orderQuantityTV.setText(new StringBuilder("Quantity: ").append( orderList.get(position).getCartItemList() == null ? "0" :
                String.valueOf(orderList.get(position).getCartItemList().size())));
        holder.orderStatusTV.setText(new StringBuilder("Status: ").append(Common.convertStatusToText(orderList.get(position).getOrderStatus())));
        holder.orderNameTV.setText(new StringBuilder("").append(orderList.get(position).getUserName()));

        holder.setListener(((view, position1) -> {
            showDialog(orderList.get(position).getCartItemList());
        }));
    }

    private void showDialog(List<CartItem> cartItemList) {
        View layoutDialog = LayoutInflater.from(context).inflate(R.layout.layout_order_details_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layoutDialog);

        Button okBtn = layoutDialog.findViewById(R.id.btn_order_detail_ok);
        RecyclerView orderDetailRV = layoutDialog.findViewById(R.id.recycler_order_details);
        orderDetailRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        orderDetailRV.setLayoutManager(linearLayoutManager);
        orderDetailRV.addItemDecoration(new DividerItemDecoration(context, linearLayoutManager.getOrientation()));

        OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(context, cartItemList);
        orderDetailRV.setAdapter(orderDetailAdapter);

        //Show dialog.
        AlertDialog dialog = builder.create();
        dialog.show();

        okBtn.setOnClickListener(view -> dialog.dismiss());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_order_date)
        TextView orderDateTV;
        @BindView(R.id.txt_order_status)
        TextView orderStatusTV;
        @BindView(R.id._txt_order_items_number)
        TextView orderQuantityTV;
        @BindView(R.id.txt_order_name)
        TextView orderNameTV;
        @BindView(R.id.img_order_item)
        ImageView orderIV;

        Unbinder unbinder;

        RecyclerClickListener listener;

        public void setListener(RecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClickListener(view, getAdapterPosition());
        }
    }
}
