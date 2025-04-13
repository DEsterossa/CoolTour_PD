package com.initiative.cmd.cooltour;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.initiative.cmd.cooltour.fragments.HistoryFragment;
import com.initiative.cmd.cooltour.fragments.MapFragment;
import com.initiative.cmd.cooltour.fragments.RoutesFragment;
import com.initiative.cmd.cooltour.fragments.UsersFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout_;
    NavigationView navigationView_;
    BottomNavigationView bottomNavigationView_;
    FragmentManager fragmentManager_;
    Toolbar toolbar_;
    RelativeLayout contentHolder_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
            openPage(menuItem.getItemId());
            return true;
        });

        contentHolder_ = findViewById(R.id.content_holder);

        fragmentManager_ = getSupportFragmentManager();
        setHeaderFragment(new MapFragment());

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        openPage(menuItem.getItemId());
        drawerLayout_.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openPage(int pageSourceId) {
        if (pageSourceId == R.id.nav_profile || pageSourceId == R.id.nav_settings) {
            Intent intent;
            if (pageSourceId == R.id.nav_profile) intent = new Intent(this, ProfileActivity.class);
            else intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return;
        }

        if (pageSourceId == R.id.nav_main || pageSourceId == R.id.bottom_nav_menu_map) {
            setHeaderFragment(new MapFragment());
        }
        else if (pageSourceId == R.id.bottom_nav_menu_routes) {
            setHeaderFragment(new RoutesFragment());
        }
        else if (pageSourceId == R.id.bottom_nav_menu_history) {
            setHeaderFragment(new HistoryFragment());
        }
        else if (pageSourceId == R.id.bottom_nav_menu_users) {
            setHeaderFragment(new UsersFragment());
        }
    }

    private void setHeaderFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager_.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
        emptyContentHolder();
    }

    private void emptyContentHolder() {
        while (contentHolder_.getChildCount() > 0) contentHolder_.removeViewAt(0);
    }
}