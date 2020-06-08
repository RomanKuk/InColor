package com.example.incolor;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.incolor.ui.color_models.Conversions;

import java.util.Random;

public class NotificationsReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "DayColor";

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("id", 0);
        Intent notificationIntent = new Intent(context, DayColorActivity.class);
        notificationIntent.putExtra("id", 100);
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        generateDayColor(context);

        SharedPreferences sharedPref = context.getSharedPreferences("COLORS", 0);
        String hexColor = sharedPref.getString("hexColor", "error");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle("Color of the Day")
                .setContentText(hexColor /*+ " " + new Date().toString()*/ + " Check it out!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationId, builder.build());
    }

    public void generateDayColor(Context context) {
        Random rand = new Random();
        int red = rand.nextInt(256);
        int green = rand.nextInt(256);
        int blue = rand.nextInt(256);
        int dayColor = Color.rgb(red, green, blue);
        String hexColor = Conversions.toHex(red, green, blue);

        SharedPreferences sharedPref = context.getSharedPreferences("COLORS", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("color", dayColor);
        editor.putString("hexColor", hexColor);
        editor.apply();
    }
}
