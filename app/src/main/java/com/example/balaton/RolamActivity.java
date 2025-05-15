package com.example.balaton;


import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RolamActivity extends AppCompatActivity {
    private TextView textViewRolam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rolam);

        textViewRolam = findViewById(R.id.textViewRolam);

        // Fade-in animáció létrehozása
        Animation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1500); // 1.5 másodperc alatt
        textViewRolam.startAnimation(fadeIn);
    }
}