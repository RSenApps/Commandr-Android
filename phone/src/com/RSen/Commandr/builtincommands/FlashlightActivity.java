package com.RSen.Commandr.builtincommands;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
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
public class FlashlightActivity extends Activity implements SurfaceHolder.Callback {
    /**
     * Holds a SurfaceView which is required for some Android phones to show the flash.
     * It is held statically because a reference must be maintained even though the activity is closed.
     */
    private static SurfaceView preview;
    /**
     * Holds a SurfaceHolder which is required for some Android phones to show the flash.
     * It is held statically because a reference must be maintained even though the activity is closed.
     */
    private static SurfaceHolder mHolder;
    /**
     * Holds a Camera which is required for some Android phones to show the flash.
     * It is held statically because a reference must be maintained even though the activity is closed.
     */
    private static Camera mCamera;
    private static boolean currentlyOn = false;

    /**
     * Called when the activity is created, based off of the intent details either turn on or off the flashlight
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("flashlight", "oncreate");
        setContentView(R.layout.activity_flashlight);
        currentlyOn = false;
        setFinishOnTouchOutside(false);
        // Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        findViewById(R.id.turnOff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            moveTaskToBack(true);
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

    /**
     * Either turns on or off the flashlight
     *
     * @param onOrOff - true for on, false for off
     */
    private void turnOnOrOff(boolean onOrOff) {
        if (onOrOff) {
            Intent i = new Intent(this, FlashlightActivity.class);
            i.putExtra("onOrOff", false);
            Notification.Builder builder = new Notification.Builder(this);
            builder.setContentTitle(getString(R.string.flashlight_activated));
            builder.setOngoing(true);
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.addAction(0, getString(R.string.turn_off), PendingIntent.getActivity(this, 193, i, 0));
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(1224, builder.build());
            if (mCamera == null) {
                preview = (SurfaceView) findViewById(R.id.PREVIEW);
                mHolder = preview.getHolder();
                mHolder.addCallback(this);
                mCamera = Camera.open();
                try {
                    mCamera.setPreviewDisplay(mHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Turn on LED
            Parameters params = mCamera.getParameters();
            if (!params.getSupportedFlashModes().contains(Parameters.FLASH_MODE_TORCH)) {
                Toast.makeText(this, getString(R.string.no_flashlight_access), Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(params);
            try {
                mCamera.startPreview();
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.no_flashlight_access), Toast.LENGTH_LONG).show();
                finish();
                return;
            }

        } else {
            // finish the activity because it can now be safely closed
            finish();
            GoogleNowUtil.resetGoogleNow(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(getIntent().putExtra("onOrOff", intent.getBooleanExtra("onOrOff", false)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean onOrOff = getIntent().getBooleanExtra("onOrOff", false);
        if (onOrOff != currentlyOn) {
            currentlyOn = onOrOff;
            turnOnOrOff(onOrOff);

        }
    }

    /**
     * Called when the activity is finished. Ensure all variables are cleaned up and flashlight is turned off.
     */
    @Override
    protected void onDestroy() {
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(1224);
        currentlyOn = false;
        if (mCamera != null) {
            // Turn off LED
            Parameters params = mCamera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(params);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        super.onDestroy();
    }

    /**
     * Empty method that is required to implement a SurfaceHolder Callback.
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    /**
     * Called when the surfaceholder is created. Add the camera to the holder
     */
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the surfaceholder is destroyed. remove the camera and clean up the holder.
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }
        mHolder = null;
    }
}
