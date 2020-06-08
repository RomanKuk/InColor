package com.example.incolor;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.incolor.ui.color_models.Conversions;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "DayColor";
    private AppBarConfiguration mAppBarConfiguration;
    static int red;
    static int blue;
    static int green;
    static int dayColor;
    static int[] hsv;
    static String hexColor;
    static SharedPreferences settings;
    static boolean active = false;
    View header;
    ConstraintLayout headerLayout;

    public MainActivity() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createNotificationChannel();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_start_color_models, R.id.nav_dialog_newton_fractal, R.id.nav_info)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        try {
            header = navigationView.getHeaderView(0);
            headerLayout = header.findViewById(R.id.headerLayout);
        } catch (NullPointerException e) {
            throw new NullPointerException("header value is null");
        }


        setColorValues();

        Intent notificationIntent = new Intent(MainActivity.this, NotificationsReceiver.class);
        notificationIntent.putExtra("id", 100);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 100,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences alarm = getSharedPreferences("ALARM", Context.MODE_PRIVATE);
        boolean first = alarm.getBoolean("check", true);
        if (first) {
            Calendar firingCall = Calendar.getInstance();
            firingCall.setTimeInMillis(System.currentTimeMillis());
            firingCall.set(Calendar.HOUR_OF_DAY, 9); //TODO change to certain time
            firingCall.set(Calendar.MINUTE, 0);
            firingCall.set(Calendar.SECOND, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                        firingCall.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, //TODO change to INTERVAL_DAY
                        pendingIntent);
            }
            alarm.edit().putBoolean("check", false).apply();
        }

        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DayColorActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "DayColorChannel";
            String description = "Channel shows color of a day";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void generateDayColor()
    {
        Random rand = new Random();
        red = rand.nextInt(256);
        green = rand.nextInt(256);
        blue = rand.nextInt(256);
        dayColor = Color.rgb(red, green, blue);
        hsv = Conversions.rgbToHsv(red, green, blue);
        hexColor = Conversions.toHex(red, green, blue);

        SharedPreferences sharedPref = getSharedPreferences("COLORS", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("color", dayColor);
        editor.putString("hexColor", hexColor);
        editor.apply();
    }

    private void setColorValues() {
        settings = getSharedPreferences("COLORS", 0);
        int lastColor = settings.getInt("color", 0);
        int stateColor = 0;
        if (lastColor == stateColor) {
            generateDayColor();
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("color", dayColor);
            editor.putString("hexColor", hexColor);
            editor.apply();
        } else {
            red = Color.red(lastColor);
            blue = Color.blue(lastColor);
            green = Color.green(lastColor);
            hexColor = Conversions.toHex(red, green, blue);
            dayColor = lastColor;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
