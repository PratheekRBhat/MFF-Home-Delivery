package com.example.mffhomedelivery.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mffhomedelivery.R;

import java.util.List;

import Adapter.PopularCategoriesAdapter;
import Model.PopularCategories;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Unbinder unbinder;
    LayoutAnimationController layoutAnimationController;

    @BindView(R.id.recycler_popular_categories)
    RecyclerView popularCategoriesRV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, root);
        init();
        homeViewModel.getPopularList().observe(getViewLifecycleOwner(), popularCategories->{

            //creating adapter
            PopularCategoriesAdapter adapter = new PopularCategoriesAdapter(getContext(), (List<PopularCategories>) popularCategories);
            popularCategoriesRV.setAdapter(adapter);
        });
        return root;
    }

    private void init() {
        popularCategoriesRV.setHasFixedSize(true);
        popularCategoriesRV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }
}
