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
import com.example.mffhomedelivery.R;

import java.util.List;

import Model.Food;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.MyViewHolder> {
    private Context context;
    private List<Food> foodList;

    public FoodListAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_food_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(foodList.get(position).getImage()).into(holder.foodIV);
        holder.foodPriceTV.setText(new StringBuilder("Rs.").append(foodList.get(position).getPrice()));
        holder.foodNameTV.setText(new StringBuilder("").append(foodList.get(position).getName()));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Unbinder unbinder;

        @BindView(R.id.img_food_image)
        ImageView foodIV;
        @BindView(R.id.txt_food_name)
        TextView foodNameTV;
        @BindView(R.id.txt_food_price)
        TextView foodPriceTV;
        @BindView(R.id.img_fav)
        ImageView favIV;
        @BindView(R.id.img_quick_cart)
        ImageView quickCartIV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
