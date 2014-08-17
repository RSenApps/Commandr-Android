package com.RSen.Commandr.util;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Ryan on 8/11/2014.
 */
public class KeyguardUtil {
    private static KeyguardManager.KeyguardLock lock;
    private static KeyguardManager.KeyguardLock getLock(Context context)
    {
        if (lock == null)
        {
            KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Activity.KEYGUARD_SERVICE);
            lock = keyguardManager.newKeyguardLock("Commandr");
        }
        return lock;
    }
    public static void lock(Context context)
    {
        getLock(context).reenableKeyguard();
    }
    public static void unlock(Context context)
    {
        getLock(context).disableKeyguard();
    }

}
