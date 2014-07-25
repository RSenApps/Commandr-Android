/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.apptentive.android.sdk.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Sky Kelsey
 */
public class Util {

    // These date formats are as close as Java can get to ISO 8601 without royally screwing up.
    public static final String PSEUDO_ISO8601_DATE_FORMAT = "yyyy-MM-dd HH:mm:ssZ"; // 2011-01-01 11:59:59-0800
    public static final String PSEUDO_ISO8601_DATE_FORMAT_MILLIS = "yyyy-MM-dd HH:mm:ss.SSSZ"; // 2011-01-01 11:59:59.123-0800 or 2011-01-01 11:59:59.23-0800

    public static String dateToIso8601String(long millis) {
        return dateToString(new SimpleDateFormat(PSEUDO_ISO8601_DATE_FORMAT_MILLIS), new Date(millis));
    }

    public static String secondsToDisplayString(String format, Double seconds) {
        String dateString = dateToString(new SimpleDateFormat(format), new Date(Math.round(seconds * 1000)));
        return dateString.replace("PM", "pm").replace("AM", "am");
    }

    public static String dateToString(DateFormat format, Date date) {
        return format.format(date);
    }

    public static Date parseIso8601Date(final String iso8601DateString) {
        // Normalize timezone.
        String s = iso8601DateString.trim().replace("Z", "+00:00").replace("T", " ");
        try {
            // Remove colon in timezone.
            if (s.charAt(s.length() - 3) == ':') {
                int lastColonIndex = s.lastIndexOf(":");
                s = s.substring(0, lastColonIndex) + s.substring(lastColonIndex + 1);
            }
            // Right pad millis to 3 places. ISO 8601 supplies fractions of seconds, but Java interprets them as millis.
            int milliStart = s.lastIndexOf('.');
            int milliEnd = (s.lastIndexOf('+') != -1) ? s.lastIndexOf('+') : s.lastIndexOf('-');
            if (milliStart != -1) {
                String start = s.substring(0, milliStart + 1);
                String millis = s.substring(milliStart + 1, milliEnd);
                String end = s.substring(milliEnd);
                millis = String.format("%-3s", millis).replace(" ", "0");
                s = start + millis + end;
            }
        } catch (Exception e) {
            Log.e("Error parsing date: " + iso8601DateString, e);
            return new Date();
        }
        // Parse, accounting for millis, if provided.
        try {
            if (s.contains(".")) {
                return new SimpleDateFormat(PSEUDO_ISO8601_DATE_FORMAT_MILLIS).parse(s);
            } else {
                return new SimpleDateFormat(PSEUDO_ISO8601_DATE_FORMAT).parse(s);
            }
        } catch (ParseException e) {
            Log.e("Exception parsing date: " + s, e);
        }

        // Return null as default. Nothing we can do but log it.
        return null;
    }

    public static int getStatusBarHeight(Window window) {
        Rect rectangle = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }


    private static List<PackageInfo> getPermissions(Context context) {
        return context.getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);
    }

    public static boolean packageHasPermission(Context context, String permission) {
        String packageName = context.getApplicationContext().getPackageName();
        return packageHasPermission(context, packageName, permission);
    }

    public static boolean packageHasPermission(Context context, String packageName, String permission) {
        List<PackageInfo> packageInfos = getPermissions(context);
        for (PackageInfo packageInfo : packageInfos) {
            if (packageInfo.packageName.equals(packageName) && packageInfo.requestedPermissions != null) {
                for (String permissionName : packageInfo.requestedPermissions) {
                    if (permissionName.equals(permission)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int pixelsToDips(Context context, int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(px / scale);
    }

    public static int dipsToPixels(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    public static float dipsToPixelsFloat(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale;
    }

    /**
     * Internal use only.
     */
    public static void hideSoftKeyboard(Activity activity, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

/*
    public void showSoftKeyboard(Activity activity, View target) {
		if (activity.getCurrentFocus() != null) {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(target, 0);
		}
	}
*/


    public static String[] getAllUserAccountEmailAddresses(Context context) {
        List<String> emails = new ArrayList<String>();
        if (Util.packageHasPermission(context, "android.permission.GET_ACCOUNTS")) {
            AccountManager accountManager = AccountManager.get(context);
            try {
                Account[] accounts = accountManager.getAccountsByType("com.google");
                for (Account account : accounts) {
                    emails.add(account.name);
                }
            } catch (VerifyError e) {
                // Ignore here because the phone is on a pre API Level 5 SDK.
            }
        }
        return emails.toArray(new String[emails.size()]);
    }

    public static String getUserEmail(Context context) {
        if (Util.packageHasPermission(context, "android.permission.GET_ACCOUNTS")) {
            String email = getEmail(context);
            if (email != null) {
                return email;
            }
        }
        return null;
    }

    private static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);
        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    // TODO: Use reflection to load this so we can drop 2.1 API requirement?
    private static Account getAccount(AccountManager accountManager) {
        Account account = null;
        try {
            Account[] accounts = accountManager.getAccountsByType("com.google");
            if (accounts.length > 0) {
                // It seems that the first google account added will always be at the end of this list. That SHOULD be the main account.
                account = accounts[accounts.length - 1];
            }
        } catch (VerifyError e) {
            // Ignore here because the phone is on a pre API Level 5 SDK.
        }
        return account;
    }

    public static boolean isNetworkConnectionPresent(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null;
    }

    public static void ensureClosed(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    public static Point getScreenSize(Context context) {
        Point ret = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        // TODO: getWidth(), getHeight(), and getOrientation() are deprecated in API 13 in favor of getSize() and getRotation().
        ret.set(display.getWidth(), display.getHeight());
        return ret;
    }

    public static void printDebugInfo(Context context) {
        // Print screen dimensions.
        // Huawei Comet: Port: PX=240x320  DP=320x427, Land: PX=320x240 DP=427x320
        // Galaxy Nexus: Port: PX=720x1184 DP=360x592, Land: PX=1196x720 DP=598x360
        // Nexus 7:      Port: PX=800x1205 DP=601x905, Land: PX=1280x736 DP=962x553
        Point point = Util.getScreenSize(context);
        Log.e("Screen size: PX=%dx%d DP=%dx%d", point.x, point.y, Util.pixelsToDips(context, point.x), Util.pixelsToDips(context, point.y));
    }

    public static boolean isEmpty(String theString) {
        return theString == null || theString.length() == 0;
    }

    public static Integer parseCacheControlHeader(String cacheControlHeader) {
        if (cacheControlHeader != null) {
            String[] cacheControlParts = cacheControlHeader.split(",");
            for (String part : cacheControlParts) {
                part = part.trim();
                if (part.startsWith("max-age=")) {
                    String[] maxAgeParts = part.split("=");
                    if (maxAgeParts.length == 2) {
                        String expiration = null;
                        try {
                            expiration = maxAgeParts[1];
                            Integer ret = Integer.parseInt(expiration);
                            return ret;
                        } catch (NumberFormatException e) {
                            Log.e("Error parsing cache expiration as number: %s", e, expiration);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean isEmailValid(String email) {
        return email.matches("^[^\\s@]+@[^\\s@]+$");
    }

    public static boolean getPackageMetaDataBoolean(Context context, String key) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getBoolean(key, false);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static Object getPackageMetaData(Context context, String key) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.get(key);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * <p>This method will allow you to pass in literal strings. You must wrap the string in single quotes in order to ensure it is not modified
     * by Android. Android will try to coerce the string to a float, Integer, etc., if it looks like one.</p>
     * <p/>
     * <p>Example: <code>&lt;meta-data android:name="sdk_distribution" android:value="'1.00'"/></code></p>
     * <p>This will evaluate to a String "1.00". If you leave off the single quotes, this method will just cast to a String, so the result would be a String "1.0".</p>
     */
    public static String getPackageMetaDataSingleQuotedString(Context context, String key) {
        Object object = getPackageMetaData(context, key);
        if (object == null) {
            return null;
        }
        String ret = object.toString();
        if (ret.endsWith("'")) {
            ret = ret.substring(0, ret.length() - 1);
        }
        if (ret.startsWith("'")) {
            ret = ret.substring(1, ret.length());
        }
        return ret;
    }

    public static String stackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Error getting app version name.", e);
        }
        return null;
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Error getting app version code.", e);
        }
        return -1;
    }

    /**
     * Converts the current time to a double representing seconds, instead of milliseconds. It will have millisecond
     * precision as fractional seconds. This is the default time format used throughout the Apptentive SDK.
     *
     * @return A double representing the current time in seconds.
     */
    public static double currentTimeSeconds() {
        long millis = System.currentTimeMillis();
        double point = (double) millis;
        return point / 1000;
    }

    public static int getUtcOffset() {
        TimeZone timezone = TimeZone.getDefault();
        return timezone.getOffset(System.currentTimeMillis()) / 1000;
    }

    public static String getInstallerPackageName(Context context) {
        try {
            return context.getPackageManager().getInstallerPackageName(context.getPackageName());
        } catch (Exception e) {
            // Just return.
        }
        return null;
    }
}
