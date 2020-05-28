package com.example.incolor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

public class DayColorActivity extends AppCompatActivity {

    static int dayColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_color);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ConstraintLayout activityLayout = findViewById(R.id.contentDayColorLayout);

        SharedPreferences sharedPref = getSharedPreferences("COLORS", Context.MODE_PRIVATE);
        dayColor = sharedPref.getInt("color", 0);
        activityLayout.setBackgroundColor(dayColor);

    }
}
