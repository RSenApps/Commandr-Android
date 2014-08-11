package com.RSen.Commandr.util;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.RSen.Commandr.core.MyAccessibilityService;

/**
 * Created by Ryan on 7/23/2014.
 */
public class GoogleNowUtil {
    private static final String GOOGLE_PKG = "com.google.android.googlequicksearchbox";
    public static void resetGoogleNow(final Context context) {
        final String home_pkg = getHomePkg(context);
        Intent i;
        PackageManager manager = context.getPackageManager();
        try {
            i = manager.getLaunchIntentForPackage(GOOGLE_PKG);
            if (i == null)
                throw new PackageManager.NameNotFoundException();
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(i);
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("closegoogle", true))
            {
                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        //service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
                        for (ActivityManager.RunningTaskInfo task : am.getRunningTasks(10))
                        {
                            String packageName = task.topActivity.getPackageName();
                            Log.d("running", packageName);
                            if (!packageName.equals(context.getPackageName()) && !packageName.equals(GOOGLE_PKG) && !packageName.equals(home_pkg) && !packageName.equals("com.android.systemui"))
                            {
                                String className = task.topActivity.getClassName();
                                Intent i = new Intent();
                                i.setClassName(packageName, className);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                try {
                                    context.startActivity(i);
                                }
                                catch (Exception e)
                                {
                                    Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                                    try {
                                        context.startActivity(LaunchIntent);
                                    }
                                    catch (Exception e1)
                                    {}
                                }
                                break;
                            }

                        }

                        return true;
                    }
                });
                handler.sendEmptyMessageDelayed(0, 1000);

            }
        } catch (PackageManager.NameNotFoundException e) {

        }

    }
    private static String getHomePkg(Context context)
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }
}
