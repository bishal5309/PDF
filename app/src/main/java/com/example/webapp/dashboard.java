package com.example.webapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import soup.neumorphism.NeumorphCardView;

public class dashboard extends AppCompatActivity {

    NeumorphCardView p1, p2, p3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        p1 = findViewById(R.id.p1);
        p2 = findViewById(R.id.p2);
        p3 = findViewById(R.id.p3);

        p1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass the asset name to MainActivity using Intent extras
                Intent pdf = new Intent(dashboard.this, MainActivity.class);
                MainActivity.asset ="b.pdf";
                startActivity(pdf);
            }
        });

        p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass the asset name to MainActivity using Intent extras
                Intent pdf = new Intent(dashboard.this, MainActivity.class);
                MainActivity.asset ="c.pdf";
                startActivity(pdf);
            }
        });

        p3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass the asset name to MainActivity using Intent extras
                Intent pdf = new Intent(dashboard.this, MainActivity.class);
                MainActivity.asset ="Bishal.pdf";
                startActivity(pdf);
            }
        });
    }
}
