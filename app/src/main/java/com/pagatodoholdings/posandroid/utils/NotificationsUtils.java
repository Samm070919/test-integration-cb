package com.pagatodoholdings.posandroid.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.pagatodoholdings.posandroid.secciones.login.LoginActivity;

public final class NotificationsUtils {

    private NotificationsUtils() {
        //no utilizado
    }

    public static void construirNotificacion
            (final Context paramContext, final int paramIcon, final String paramTitulo, final String paramMsg) {

        final Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationManager administradorNotificaciones = (NotificationManager) paramContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(paramContext, LoginActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(paramContext, 0,
                notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constantes.NOTIFICATIONS_CHANNEL_ID, Constantes.NOTIFICATIONS_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setLightColor(Color.BLUE);
            channel.enableLights(true);
            channel.setSound(
                    alarmSound,
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build()
            );
            channel.setShowBadge(true);
            administradorNotificaciones.createNotificationChannel(channel);
        }


        final NotificationCompat.Builder contructorNotificaciones =
                new NotificationCompat.Builder(paramContext, "chanelid")
                        .setSmallIcon(paramIcon)
                        .setContentTitle(paramTitulo)
                        .setContentText(paramMsg)
                        .setSound(alarmSound)
                        .setVibrate(new long[]{500, 500})
                        .setLights(Color.WHITE, 3000, 3000)
                        .setAutoCancel(true);

        assert administradorNotificaciones != null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            contructorNotificaciones.setChannelId(Constantes.NOTIFICATIONS_CHANNEL_ID);
        }

        contructorNotificaciones.setContentIntent(intent);
        administradorNotificaciones.notify(112, contructorNotificaciones.build());

    }
}