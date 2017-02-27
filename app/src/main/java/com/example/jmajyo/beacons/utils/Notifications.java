package com.example.jmajyo.beacons.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class Notifications {
    public static void postNotification(Context context, Class<? extends Activity> activity, String titulo, String texto, int icono, int color, int identificator) {
        Intent i = new Intent(context, activity);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 5, i, 0);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setSmallIcon(icono)
                .setAutoCancel(true)
                .setColor(color)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(identificator, notification);
    }
}
