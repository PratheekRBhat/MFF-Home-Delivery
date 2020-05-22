package com.example.mffhomedelivery.ui.menu;

import android.app.AlertDialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mffhomedelivery.Adapter.CategoryAdapter;
import com.example.mffhomedelivery.Common.Common;
import com.example.mffhomedelivery.Common.SpacesItemDecoration;
import com.example.mffhomedelivery.EventBus.MenuItemBack;
import com.example.mffhomedelivery.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import Model.Category;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class MenuFragment extends Fragment {

    private MenuViewModel menuViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_menu)
    RecyclerView menuRV;
    AlertDialog alertDialog;
    LayoutAnimationController layoutAnimationController;
    CategoryAdapter categoryAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        menuViewModel =
                ViewModelProviders.of(this).get(MenuViewModel.class);
        View root = inflater.inflate(R.layout.fragment_menu, container, false);

        unbinder = ButterKnife.bind(this, root);
        initViews();
        menuViewModel.getMessageError().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });
        menuViewModel.getCategoryListMutable().observe(getViewLifecycleOwner(), categories -> {
            alertDialog.dismiss();
            categoryAdapter = new CategoryAdapter(getContext(), categories);
            menuRV.setAdapter(categoryAdapter);
            menuRV.setLayoutAnimation(layoutAnimationController);
        });
        return root;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }

    private void initViews() {
        setHasOptionsMenu(true);

        alertDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        alertDialog.show();

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (categoryAdapter != null) {
                    switch (categoryAdapter.getItemViewType(position)){
                        case Common.DEFAULT_COLUMN_COUNT: return 1;
                        case Common.FULL_WIDTH_COLUMN: return 2;
                        default: return -1;
                    }
                }
                return -1;
            }
        });
        menuRV.setLayoutManager(layoutManager);
        menuRV.addItemDecoration(new SpacesItemDecoration(8));

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

            menuViewModel.loadCategories();     //Restore result to the default values.
        });

    }

    private void startSearch(String s) {
        List<Category> resultList = new ArrayList<>();

        for (int i = 0; i<categoryAdapter.getListCategory().size(); i++) {
            Category categoryItem = categoryAdapter.getListCategory().get(i);
            if (categoryItem.getName().toLowerCase().contains(s)) {
                resultList.add(categoryItem);
            }
        }
        menuViewModel.getCategoryListMutable().setValue(resultList);
    }
}
