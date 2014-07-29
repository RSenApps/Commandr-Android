package com.RSen.Commandr.util;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.RSen.Commandr.core.MyAccessibilityService;

/**
 * Created by Ryan on 7/23/2014.
 */
public class GoogleNowUtil {
    public static void resetGoogleNow(Context context) {

        Intent i;
        PackageManager manager = context.getPackageManager();
        try {
            i = manager.getLaunchIntentForPackage("com.google.android.googlequicksearchbox");
            if (i == null)
                throw new PackageManager.NameNotFoundException();
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(i);
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("closegoogle", true) && context instanceof MyAccessibilityService)
            {
                final MyAccessibilityService service = (MyAccessibilityService) context;
                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        return true;
                    }
                });
                handler.sendEmptyMessageDelayed(0, 1000);

            }
        } catch (PackageManager.NameNotFoundException e) {

        }

    }
}
