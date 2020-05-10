package com.example.mffhomedelivery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.mffhomedelivery.Database.CartItem;
import com.example.mffhomedelivery.EventBus.UpdateItemInCart;
import com.example.mffhomedelivery.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    private Context context;
    private List<CartItem> cartItemList;

    public CartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(cartItemList.get(position).getFoodImage())
                .into(holder.cartFoodIV);
        holder.foodNameTV.setText(new StringBuilder(cartItemList.get(position).getFoodName()));
        holder.foodPriceTV.setText(new StringBuilder("").append(cartItemList.get(position).getFoodPrice()));
        holder.elegantNumberButton.setNumber(String.valueOf(cartItemList.get(position).getFoodQuantity()));

        //Event.
        holder.elegantNumberButton.setOnValueChangeListener((view, oldValue, newValue) -> {
            cartItemList.get(position).setFoodQuantity(newValue);
            EventBus.getDefault().postSticky(new UpdateItemInCart(cartItemList.get(position)));
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Unbinder unbinder;
        @BindView(R.id.img_cart_food_item)
        ImageView cartFoodIV;
        @BindView(R.id.txt_cart_food_name)
        TextView foodNameTV;
        @BindView(R.id.txt_cart_food_price)
        TextView foodPriceTV;
        @BindView(R.id.elegantNumberButton)
        ElegantNumberButton elegantNumberButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
