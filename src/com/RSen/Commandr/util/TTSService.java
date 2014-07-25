package com.RSen.Commandr.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TTSService extends Service {
    TTSHelper helper;

    public TTSService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            helper.stop();
        } catch (Exception e) {
        }
        if (intent != null) {
            helper = new TTSHelper(this, intent.getStringExtra("toSpeak"));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
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
