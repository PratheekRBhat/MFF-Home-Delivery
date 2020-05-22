package com.example.mffhomedelivery.ui.foodlist;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mffhomedelivery.Adapter.FoodListAdapter;
import com.example.mffhomedelivery.Common.Common;
import com.example.mffhomedelivery.EventBus.MenuItemBack;
import com.example.mffhomedelivery.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import Model.Food;
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
            if (food != null) {
                foodListAdapter = new FoodListAdapter(getContext(), food);
                foodListRV.setAdapter(foodListAdapter);
                foodListRV.setLayoutAnimation(layoutAnimationController);
            }
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

        setHasOptionsMenu(true);

        foodListRV.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        foodListRV.setLayoutManager(layoutManager);
        foodListRV.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));  //To add dividers between list items.

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        //Event.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                startSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        //Clear text when clear button in Search View is clicked.
        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(view -> {
            EditText ed = searchView.findViewById(R.id.search_src_text);

            ed.setText("");     //Clear text.
            searchView.setQuery("", false);     //Clear query.
            searchView.onActionViewCollapsed();     //Collapse teh action view.
            menuItem.collapseActionView();      //Collapse the search widget.

            foodListViewModel.getMutableLiveDataFoodList();     //Restore result to the default values.
        });

    }

    private void startSearch(String s) {
        List<Food> resultList = new ArrayList<>();

        for (int i = 0; i<Common.categorySelected.getFoods().size(); i++) {
            Food foodItem = Common.categorySelected.getFoods().get(i);
            if (foodItem.getName().toLowerCase().contains(s)) {
                resultList.add(foodItem);
            }
        }
        foodListViewModel.getMutableLiveDataFoodList().setValue(resultList);
    }
}
