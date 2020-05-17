package com.example.mffhomedelivery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mffhomedelivery.Common.Common;
import com.example.mffhomedelivery.Database.CartDataSource;
import com.example.mffhomedelivery.Database.CartDatabase;
import com.example.mffhomedelivery.Database.CartItem;
import com.example.mffhomedelivery.Database.LocalCartDataSource;
import com.example.mffhomedelivery.EventBus.CounterCartEvent;
import com.example.mffhomedelivery.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import Model.Food;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.MyViewHolder> {
    private Context context;
    private List<Food> foodList;
    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;

    public FoodListAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
        this.compositeDisposable = new CompositeDisposable();
        this.cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).CartDao());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_best_deals_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(foodList.get(position).getImage()).into(holder.foodIV);
        holder.foodPriceTV.setText(new StringBuilder("Total: \u20B9").append(foodList.get(position).getPrice()));
        holder.foodNameTV.setText(new StringBuilder("").append(foodList.get(position).getName()));
        holder.foodCategoryTV.setText(new StringBuilder("").append(Common.categorySelected.getName()));
        holder.vegetarianTV.setText(R.string.vegetarian);

        holder.quickCartIV.setOnClickListener(view -> {
            CartItem cartItem = new CartItem();
            cartItem.setUid(Common.currentUser.getUid());
            cartItem.setUserPhone(Common.currentUser.getPhone());

            cartItem.setFoodID(foodList.get(position).getId());
            cartItem.setFoodName(foodList.get(position).getName());
            cartItem.setFoodImage(foodList.get(position).getImage());
            cartItem.setFoodPrice(Double.parseDouble(String.valueOf(foodList.get(position).getPrice())));
            cartItem.setFoodQuantity(1);

           cartDataSource.getItemInCart(Common.currentUser.getUid(), cartItem.getFoodID())
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new SingleObserver<CartItem>() {
                       @Override
                       public void onSubscribe(Disposable d) {

                       }

                       @Override
                       public void onSuccess(CartItem cartItemFromDB) {
                           if (cartItemFromDB.equals(cartItem)) {
                               //Item already in database. So just update it in the cart.
                               cartItemFromDB.setFoodQuantity(cartItemFromDB.getFoodQuantity() + cartItem.getFoodQuantity());

                               cartDataSource.updateCartItems(cartItemFromDB)
                                       .subscribeOn(Schedulers.io())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .subscribe(new SingleObserver<Integer>() {
                                           @Override
                                           public void onSubscribe(Disposable d) {

                                           }

                                           @Override
                                           public void onSuccess(Integer integer) {
                                               Toast.makeText(context, "Cart Updated", Toast.LENGTH_SHORT).show();
                                               EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                           }

                                           @Override
                                           public void onError(Throwable e) {
                                               Toast.makeText(context, "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                           }
                                       });
                           } else {
                               //Item not in cart. Insert new.
                               compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                       .subscribeOn(Schedulers.io())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .subscribe(() -> {
                                           Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                                           EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                       }, throwable -> {
                                           Toast.makeText(context, "CART ERROR" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                       }));
                           }
                       }

                       @Override
                       public void onError(Throwable e) {
                           if (e.getMessage().contains("empty")) {
                               compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                       .subscribeOn(Schedulers.io())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .subscribe(() -> {
                                           Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                                           EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                       }, throwable -> {
                                           Toast.makeText(context, "CART ERROR" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                       }));
                           } else
                                Toast.makeText(context, "[GET CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Unbinder unbinder;

        @BindView(R.id.img_best_deals_food_image)
        ImageView foodIV;
        @BindView(R.id.txt_best_deals_food_name)
        TextView foodNameTV;
        @BindView(R.id.txt_best_deals_food_price)
        TextView foodPriceTV;
        @BindView(R.id.img_best_deals_quick_cart)
        ImageView quickCartIV;
        @BindView(R.id.txt_food_category)
        TextView foodCategoryTV;
        @BindView(R.id.vegetarian)
        TextView vegetarianTV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
