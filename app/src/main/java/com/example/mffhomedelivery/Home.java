package com.example.mffhomedelivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.andremion.counterfab.CounterFab;
import com.example.mffhomedelivery.Common.Common;
import com.example.mffhomedelivery.Database.CartDataSource;
import com.example.mffhomedelivery.Database.CartDatabase;
import com.example.mffhomedelivery.Database.LocalCartDataSource;
import com.example.mffhomedelivery.EventBus.CategoryClick;
import com.example.mffhomedelivery.EventBus.CounterCartEvent;
import com.example.mffhomedelivery.EventBus.HideFABCart;
import com.example.mffhomedelivery.EventBus.MenuItemBack;
import com.example.mffhomedelivery.EventBus.PopularCategoryClick;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import Model.Category;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawerLayout;
    private NavController navController;

    android.app.AlertDialog dialog;

    @BindView(R.id.fab)
    CounterFab counterFab;

    int menuClickId = -1;

    private CartDataSource cartDataSource;

    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        ButterKnife.bind(this);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).CartDao());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            countCartItem();
            navController.navigate(R.id.nav_cart);
        });
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_menu, R.id.nav_foodList, R.id.nav_cart)
                .setDrawerLayout(drawerLayout)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        //To set the header of the navigation bar.
        View headerView = navigationView.getHeaderView(0);
        TextView txt_user = headerView.findViewById(R.id.txt_user);
        Common.setSpanString("Hey, ",  Common.currentUser.getName(), txt_user);

        countCartItem();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        drawerLayout.closeDrawers();

        switch (item.getItemId()) {
            case R.id.nav_home:
                if(item.getItemId() != menuClickId) {
                    navController.navigate(R.id.nav_home);
                }
                break;
            case R.id.nav_menu:
                if(item.getItemId() != menuClickId) {
                    navController.navigate(R.id.nav_menu);
                }
                break;
            case R.id.nav_cart:
                if(item.getItemId() != menuClickId) {
                    navController.navigate(R.id.nav_cart);
                }break;
        }
        menuClickId = item.getItemId();

        return true;
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign Out")
                .setMessage("Confirm sign out?")
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    Common.categorySelected = null;
                    Common.currentUser = null;
                    FirebaseAuth.getInstance().signOut();

                    Intent signOutIntent = new Intent(Home.this, MainActivity.class);
                    signOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(signOutIntent);
                    finish();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        //Receive notification from CategoryAdapter.
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        //Receive notification from CategoryAdapter.
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    //Navigate to Food List using EventBus for communication.
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCategorySelected(CategoryClick event) {
        if (event.isSuccess()) {
            navController.navigate(R.id.nav_foodList);
//            Toast.makeText(this, "Click to "+event.getCategory().getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCartCounter(CounterCartEvent event) {
        if (event.isSuccess()) {
            countCartItem();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPopularItemClick(PopularCategoryClick event) {
        if (event.getPopularCategories() != null) {
            dialog.show();

            FirebaseDatabase.getInstance()
                    .getReference(Common.CATEGORY_REF)
                    .child(event.getPopularCategories().getMenu_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                Common.categorySelected = dataSnapshot.getValue(Category.class);

                                //Load food items.
                                navController.navigate(R.id.nav_foodList);
                                dialog.dismiss();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(Home.this, "Item does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            dialog.dismiss();
                            Toast.makeText(Home.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void countCartItem() {
        cartDataSource.countItemsInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        counterFab.setCount(integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!e.getMessage().contains("Query returned empty")) {
                            Toast.makeText(Home.this, "[COUNT CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else
                            counterFab.setCount(0);
                    }
                });
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onHideFABEvent(HideFABCart event) {
        if(event.isHidden())
        {
            counterFab.hide();
        }
        else
            counterFab.show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMenuItemBack(MenuItemBack event) {
        menuClickId = -1;
        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
    }
}
