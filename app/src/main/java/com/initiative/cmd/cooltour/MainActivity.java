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
import android.widget.TextView;

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
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapWindow;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.geometry.Point;
import com.yandex.runtime.image.ImageProvider;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout_;
    NavigationView navigationView_;
    BottomNavigationView bottomNavigationView_;
    FragmentManager fragmentManager_;
    Toolbar toolbar_;
    RelativeLayout contentHolder_;
    MapView mapView_;

    CameraListener camListener_ = (map, cameraPosition, cameraUpdateReason, b) -> {
        Point pt = cameraPosition.getTarget();
        TextView tv = findViewById(R.id.map_info);

        String txt = "Latitude: " + pt.getLatitude() + "\n" +
                "Longitude: " + pt.getLongitude() + "\n";
        tv.setText(txt);
    };

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
            mapView_ = findViewById(R.id.map_view);

            ImageButton zoomIn = findViewById(R.id.zoom_in);
            ImageButton zoomOut = findViewById(R.id.zoom_out);

            MapWindow mapWindow = mapView_.getMapWindow();
            Map map = mapWindow.getMap();
            map.addCameraListener(camListener_);
            zoomIn.setOnClickListener(v -> zoomIn(1f));
            zoomOut.setOnClickListener(v -> zoomOut(1f));
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

    private PlacemarkMapObject addPlacemark(
            Point pt,
            ImageProvider icon,
            String text,
            boolean moveToPt
    ) {
        MapWindow mapWindow = mapView_.getMapWindow();
        Map map = mapWindow.getMap();
        MapObjectCollection mapObjects = map.getMapObjects();

        if (moveToPt) {
            CameraPosition camPos = new CameraPosition(
                    pt,
                    map.getCameraPosition().getZoom(),
                    map.getCameraPosition().getAzimuth(),
                    map.getCameraPosition().getTilt()
            );

            map.move(camPos);
        }

        PlacemarkMapObject placemarkMapObject = mapObjects.addPlacemark();
        placemarkMapObject.setGeometry(pt);
        placemarkMapObject.setIcon(icon);
        placemarkMapObject.setText(text);
        return placemarkMapObject;
    }

    private PlacemarkMapObject addPlacemark(
            Point pt,
            ImageProvider icon,
            String text
    ) {
        return addPlacemark(pt, icon, text, false);
    }

    private void zoomIn(
            float delta
    ) {
        MapWindow mapWindow = mapView_.getMapWindow();
        Map map = mapWindow.getMap();
        CameraPosition camPos = map.getCameraPosition();
        CameraPosition newCamPos = new CameraPosition(
                camPos.getTarget(),
                camPos.getZoom() + delta,
                camPos.getAzimuth(),
                camPos.getTilt()
        );

        map.move(newCamPos, new Animation(Animation.Type.SMOOTH, 0.3f), null);
    }

    private void zoomOut(
            float delta
    ) {
        zoomIn(-delta);
    }
}