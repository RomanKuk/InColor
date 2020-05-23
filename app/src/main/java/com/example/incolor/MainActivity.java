package com.example.incolor;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    private AppBarConfiguration mAppBarConfiguration;
    private TextView txtRgb;
    private TextView txtHex;
    private TextView txtHsv;
    private LinearLayout headerLayout;
    /*private PendingIntent myPendingIntent;
    private AlarmManager alarmManager;
    private BroadcastReceiver myBroadcastReceiver;
    private Calendar firingCal;*/

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_start_color_models, R.id.nav_dialog_newton_fractal, R.id.nav_info)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /*firingCal= Calendar.getInstance();
        firingCal.set(Calendar.HOUR, 8); // At the hour you want to fire the alarm
        firingCal.set(Calendar.MINUTE, 0); // alarm minute
        firingCal.set(Calendar.SECOND, 0); // and alarm second
        long intendedTime = firingCal.getTimeInMillis();

        registerMyAlarmBroadcast();
        alarmManager.set( AlarmManager.RTC_WAKEUP, intendedTime, myPendingIntent );
        //alarmManager.set()*/
        View header = navigationView.getHeaderView(0);
        txtHex = header.findViewById(R.id.txtColorHex);
        txtRgb = header.findViewById(R.id.txtColorRgb);
        txtHsv = header.findViewById(R.id.txtColorHsv);
        headerLayout = header.findViewById(R.id.headerLayout);

        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        SharedPreferences settings = getSharedPreferences("PREFS", 0);
        int lastDay = settings.getInt("day", 0);
        int minute = calendar.get(Calendar.MINUTE);
        int lastMinute = settings.getInt("minute", 0);

        if(/*lastDay != currentDay*/lastMinute != minute) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("day", currentDay);
            editor.putInt("minute", minute);
            editor.apply();

            Random rand = new Random();
            int red = rand.nextInt(256);
            int green = rand.nextInt(256);
            int blue = rand.nextInt(256);
            int dayColor = Color.rgb(red, green, blue);
            float[] hsv = new float[3];
            Color.colorToHSV(dayColor, hsv);
            String hexColor = Conversions.toHex(red, green, blue);
            txtHex.setText(hexColor);
            txtRgb.setText("rgb("+red+", "+green+", "+blue+")");
            txtHsv.setText("hsv("+(int)hsv[0]+"º, "+(int)(hsv[1]*100)+"%, "+(int)(hsv[2]*100)+"%)");
            headerLayout.setBackgroundColor(dayColor);

            /*NotificationManager notifyManager =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify = new Notification.Builder
                    (getApplicationContext()).setContentTitle("Test Push").
                    setContentText("Check out a new color of the day!").
                    setContentTitle("InColor").setSmallIcon(R.drawable.logo).build();

            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notifyManager.notify(0, notify);*/
        }
    }


    /*private void registerMyAlarmBroadcast()
    {
        //Log.i(TAG, "Going to register Intent.RegisterAlramBroadcast");

        //This is the call back function(BroadcastReceiver) which will be call when your
        //alarm time will reached.
        myBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                //Log.i(TAG,"BroadcastReceiver::OnReceive()");
                Toast.makeText(context, "Your Alarm is there", Toast.LENGTH_LONG).show();
            }
        };

        registerReceiver(myBroadcastReceiver, new IntentFilter("com.alarm.example") );
        myPendingIntent = PendingIntent.getBroadcast( this, 0, new Intent("com.alarm.example"),0 );
        alarmManager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
    }
    private void UnregisterAlarmBroadcast()
    {
        alarmManager.cancel(myPendingIntent);
        getBaseContext().unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();
    }*/

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
