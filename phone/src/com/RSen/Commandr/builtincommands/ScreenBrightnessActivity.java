package com.RSen.Commandr.builtincommands;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.util.GoogleNowUtil;

import java.io.IOException;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightActivity.java
 * @version 1.0
 *          5/28/14
 */
public class ScreenBrightnessActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = getIntent().getIntExtra("brightness", 50)/100.0f;
        getWindow().setAttributes(lp);
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                finish();
                GoogleNowUtil.resetGoogleNow(ScreenBrightnessActivity.this);
                return true;
            }
        });
        handler.sendEmptyMessageDelayed(0, 300);
    }
}
