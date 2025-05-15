package com.example.balaton;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class MapActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar topAppBar;
    private ImageView hajoImage;
    private ImageView mapImage;
    private CoordinatorLayout mapRootLayout;

    // xRatio, yRatio, name
    Object[][] dotData = {
            {0.36f, 0.44f, "Tapolca"},
            {0.65f, 0.72f, "Zamárdi"},
            {0.44f, 0.63f, "Füred"},
            {0.17f, 0.39f, "Keszthely"},
            {0.32f, 0.61f, "Veszprém"},
            {0.17f, 0.41f, "Balatonlelle"}
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        hajoImage = findViewById(R.id.hajoImage);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        topAppBar = findViewById(R.id.material_toolbar);

        // Képernyő szélesség lekérdezése
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();

        // Hamburger ikon megnyitja a drawert
        topAppBar.setNavigationOnClickListener(v -> drawerLayout.open());

        // Menüelemek kezelése
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                Toast.makeText(this, "Keresés", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Animáció létrehozása: a hajó mozog a képernyő széltől szélig
        ObjectAnimator animator = ObjectAnimator.ofFloat(hajoImage, "translationX", 0, screenWidth - 100);
        animator.setDuration(5000); // 5 másodperc
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();


        // Drawer menüelemek kezelése
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Toast.makeText(MapActivity.this, "Kezdőlap", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.nav_profile) {
                    Toast.makeText(MapActivity.this, "Profil", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.nav_logout) {
                    Toast.makeText(MapActivity.this, "Kijelentkezés", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (id == R.id.nav_latnivalok) {
                    Intent intent = new Intent(MapActivity.this, LatnivaloActivity.class);
                    startActivity(intent);
                    return true;
                }
                else if (id == R.id.nav_gps) {
                    Intent intent = new Intent(MapActivity.this, GPSActivity.class);
                    startActivity(intent);
                    return true;
                }else if (id == R.id.nav_rolam) {
                    Intent intent = new Intent(MapActivity.this, RolamActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });

        mapImage = findViewById(R.id.mapImage);
        mapRootLayout = findViewById(R.id.layout);

        mapImage.post(() -> {
            int imageWidth = mapImage.getWidth();
            int imageHeight = mapImage.getHeight();

            for (Object[] dot : dotData) {
                float xRatio = (float) dot[0];
                float yRatio = (float) dot[1];
                String name = (String) dot[2];

                View dotView = new View(this);
                dotView.setBackgroundResource(R.drawable.dot_background);
                dotView.setTag(name); // itt eltároljuk a város nevét

                dotView.setOnClickListener(this::onDotClick);

                int dotSize = 32; // px
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dotSize, dotSize);
                params.leftMargin = (int) (xRatio * imageWidth) - dotSize / 2;
                params.topMargin = (int) (yRatio * imageHeight) - dotSize / 2;

                mapRootLayout.addView(dotView, params);
            }
        });



    }

    private void onDotClick(View view) {
        String varos = (String) view.getTag(); // lekérjük a nevet
        Toast.makeText(this, varos, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LatnivaloActivity.class);
        intent.putExtra("varosSzuro", varos);
        intent.putExtra("csakKedvencek", false);
        startActivity(intent);

    }
}
