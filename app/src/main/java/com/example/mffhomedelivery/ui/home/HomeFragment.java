package com.example.mffhomedelivery.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mffhomedelivery.Adapter.BestDealsAdapter;
import com.example.mffhomedelivery.Adapter.PopularCategoriesAdapter;
import com.example.mffhomedelivery.R;

import java.util.List;

import Model.PopularCategories;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Unbinder unbinder;
    @BindView(R.id.recycler_best_deals)
    RecyclerView bestDealsRV;
    AlertDialog alertDialog;

    LayoutAnimationController layoutAnimationController;

    @BindView(R.id.recycler_popular_categories)
    RecyclerView popularCategoriesRV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, root);

        //Method to initialise the recycler view.
        initViews();

        homeViewModel.getPopularList().observe(getViewLifecycleOwner(), popularCategories->{
            alertDialog.dismiss();
            //creating Popular Categories RecyclerView adapter.
            PopularCategoriesAdapter popularCategoriesAdapter = new PopularCategoriesAdapter(getContext(), (List<PopularCategories>) popularCategories);
            popularCategoriesRV.setAdapter(popularCategoriesAdapter);
            popularCategoriesRV.setLayoutAnimation(layoutAnimationController);
        });

        homeViewModel.getBestDealList().observe(getViewLifecycleOwner(),bestDeals -> {
            //creating Best Deals RecyclerView adapter.
            BestDealsAdapter bestDealsAdapter = new BestDealsAdapter(getContext(), bestDeals);
            bestDealsRV.setAdapter(bestDealsAdapter);
            bestDealsRV.setLayoutAnimation(layoutAnimationController);
        });
        return root;
    }

    private void initViews() {
        alertDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        alertDialog.show();

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        popularCategoriesRV.setHasFixedSize(true);
        popularCategoriesRV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        bestDealsRV.setHasFixedSize(true);
        bestDealsRV.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
