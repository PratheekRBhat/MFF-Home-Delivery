package com.example.mffhomedelivery.ui.foodlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mffhomedelivery.Adapter.FoodListAdapter;
import com.example.mffhomedelivery.Common.Common;
import com.example.mffhomedelivery.EventBus.MenuItemBack;
import com.example.mffhomedelivery.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FoodListFragment extends Fragment {

    private FoodListViewModel foodListViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_food_list)
    RecyclerView foodListRV;
    private FoodListAdapter foodListAdapter;

    private LayoutAnimationController layoutAnimationController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        foodListViewModel =
                ViewModelProviders.of(this).get(FoodListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_food_list, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();
        foodListViewModel.getMutableLiveDataFoodList().observe(getViewLifecycleOwner(), food -> {
            foodListAdapter = new FoodListAdapter(getContext(), food);
            foodListRV.setAdapter(foodListAdapter);
            foodListRV.setLayoutAnimation(layoutAnimationController);
        });
        return root;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }

    private void initViews() {
        //Set action bar to show the title of the food category.
        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.categorySelected.getName());

        foodListRV.setHasFixedSize(true);
        foodListRV.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
    }
}
