package com.RSen.Commandr.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.RSen.Commandr.R;
import com.RSen.Commandr.ui.activity.MainActivity;

public class TTSService extends Service {
    TTSHelper helper;

    public TTSService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null && intent.getAction().equals("STOP")) {
            stopSelf();
        } else {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setContentTitle(getString(R.string.commandr_speaking));
            builder.setContentIntent(PendingIntent.getActivity(this, 12343,
                    new Intent(this, MainActivity.class), 0));
            builder.setOngoing(true);
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.addAction(0, getString(R.string.stop),
                    PendingIntent.getService(this, 51251, new Intent(getApplicationContext(),
                            TTSService.class).setAction("STOP"), 0)
            );
            startForeground(12342, builder.build());
            try {
                helper.stop();
            } catch (Exception e) {
            }
            if (intent != null) {
                helper = new TTSHelper(this, intent.getStringExtra("toSpeak"));
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        try {
            helper.stop();
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
