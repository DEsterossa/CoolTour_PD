package com.initiative.cmd.cooltour;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.initiative.cmd.cooltour.fragments.HistoryHeaderFragment;
import com.initiative.cmd.cooltour.fragments.MapHeaderFragment;
import com.initiative.cmd.cooltour.fragments.ProfileHeaderFragment;
import com.initiative.cmd.cooltour.fragments.RoutesHeaderFragment;
import com.initiative.cmd.cooltour.fragments.SettingsHeaderFragment;
import com.initiative.cmd.cooltour.fragments.UsersHeaderFragment;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.mapview.MapView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout_;
    NavigationView navigationView_;
    BottomNavigationView bottomNavigationView_;
    FragmentManager fragmentManager_;
    Toolbar toolbar_;
    RelativeLayout contentHolder_;
    MapView mapView_;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout_.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout_.closeDrawer(GravityCompat.START);
                }
                else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        navigationView_ = findViewById(R.id.nav_view);
        navigationView_.setNavigationItemSelectedListener(this);
        navigationView_.setCheckedItem(R.id.nav_main);

        toolbar_ = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar_);

        drawerLayout_ = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout_, toolbar_,
                R.string.nav_drawer_open, R.string.nav_drawer_close
        );
        drawerLayout_.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        bottomNavigationView_ = findViewById(R.id.bottom_nav);
        bottomNavigationView_.setBackground(null);
        bottomNavigationView_.setOnItemSelectedListener(menuItem -> {
            switchToPage(menuItem.getItemId());
            return true;
        });

        contentHolder_ = findViewById(R.id.content_holder);

        mapView_ = LayoutInflater.from(this).inflate(R.layout.page_map, null).findViewById(R.id.map_view);

        fragmentManager_ = getSupportFragmentManager();
        switchToPage(R.id.bottom_nav_menu_map);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView_.onStart();
    }

    @Override
    protected void onStop() {
        mapView_.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switchToPage(menuItem.getItemId());
        drawerLayout_.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("InflateParams")
    private void switchToPage(int pgSrcId) {
        emptyContentHolder();
        loadPageContent(pgSrcId);
        loadPageScript(pgSrcId);
    }

    private void loadPageContent(int pgSrcId) {
        int pgId = -1;
        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        if (pgSrcId == R.id.nav_main || pgSrcId == R.id.bottom_nav_menu_map) {
            setHeaderFragment(new MapHeaderFragment());
            bottomAppBar.setVisibility(View.VISIBLE);
            checkBtmMenuItem(0);

            pgId = R.layout.page_map;
        }
        else if (pgSrcId == R.id.bottom_nav_menu_routes) {
            setHeaderFragment(new RoutesHeaderFragment());
            bottomAppBar.setVisibility(View.VISIBLE);

            pgId = R.layout.page_routes;
        }
        else if (pgSrcId == R.id.bottom_nav_menu_history) {
            setHeaderFragment(new HistoryHeaderFragment());
            bottomAppBar.setVisibility(View.VISIBLE);

            pgId = R.layout.page_history;
        }
        else if (pgSrcId == R.id.bottom_nav_menu_users) {
            setHeaderFragment(new UsersHeaderFragment());
            bottomAppBar.setVisibility(View.VISIBLE);

            pgId = R.layout.page_users;
        }
        else if (pgSrcId == R.id.nav_profile) {
            setHeaderFragment(new ProfileHeaderFragment());
            bottomAppBar.setVisibility(View.GONE);

            pgId = R.layout.page_profile;
        }
        else if (pgSrcId == R.id.nav_settings) {
            setHeaderFragment(new SettingsHeaderFragment());
            bottomAppBar.setVisibility(View.GONE);

            pgId = R.layout.page_settings;
        }

        if (pgId != -1) {
            View v = LayoutInflater.from(this).inflate(pgId, null);
            contentHolder_.addView(v, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
        }
    }

    private void loadPageScript(int pgSrcId) {
        if (pgSrcId == R.id.nav_main || pgSrcId == R.id.bottom_nav_menu_map) {
            ImageButton zoomIn = findViewById(R.id.zoom_in);
            ImageButton zoomOut = findViewById(R.id.zoom_out);

            zoomIn.setOnClickListener(v -> {

            });

            zoomOut.setOnClickListener(v -> {

            });
        }
    }

    private void setHeaderFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager_.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void emptyContentHolder() {
        while (contentHolder_.getChildCount() > 0) contentHolder_.removeViewAt(0);
    }

    private void checkBtmMenuItem(int itemIndex) {
        Menu menu = bottomNavigationView_.getMenu();
        for (int i = 0; i < menu.size(); i++) menu.getItem(i).setChecked(false);
        if (itemIndex >= 0) menu.getItem(itemIndex).setChecked(true);
    }
}