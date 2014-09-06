package com.RSen.Commandr.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.RSen.Commandr.R;
import com.RSen.Commandr.ui.activity.MainActivity;
import com.apptentive.android.sdk.Apptentive;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Ryan on 6/7/2014.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        Bundle extras = intent.getExtras();
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Apptentive.setPendingPushNotification(context, intent);
                Notification.Builder builder = new Notification.Builder(context);
                builder.setContentTitle(context.getString(R.string.support_message));
                builder.setContentText(context.getString(R.string.new_message));
                builder.setSmallIcon(R.drawable.ic_launcher);
                builder.setAutoCancel(true);
                //builder.setContentIntent(PendingIntent.getBroadcast(context, 3531, new Intent("com.RSen.OpenMic.Pheonix.MESSAGE_OPENED"), 0));
                builder.setContentIntent(PendingIntent.getActivity(context, 123, new Intent(context, MainActivity.class), 0));
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1232, builder.build());
            }
        }
    }
}
