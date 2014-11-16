package com.RSen.Commandr.ui.activity;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.core.MostWantedCommands;
import com.RSen.Commandr.core.TaskerCommand;
import com.RSen.Commandr.core.TaskerCommands;
import com.RSen.Commandr.ui.fragment.SettingsFragment;
import com.RSen.Commandr.util.WearUtil;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.apptentive.android.sdk.Apptentive;
import com.apptentive.android.sdk.ApptentiveActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         MainActivity.java
 * @version 1.0
 *          5/28/14
 */
public class MainActivity extends ActionBarActivity {
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    String SENDER_ID = "849128011795";
    GoogleCloudMessaging gcm;
    String regid;

    public static Map<String, String> getSharedPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, String> map = new HashMap<String, String>();
        for (String key : prefs.getAll().keySet()) {
            Object value = prefs.getAll().get(key);
            if (value instanceof Set) {
                map.put(key, Arrays.toString(((Set) value).toArray()));
            } else {
                map.put(key, value.toString());
            }
        }

        return map;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Apptentive.onStart(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Apptentive.onStop(this);
    }
    /**
     * Called when the activity is created, add the Settings fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("setup", false)) {
            Intent i = new Intent(this, SetupActivity.class);
            startActivity(i);
            finish();
        }
        WearUtil.updateCommandList(this);

        setContentView(R.layout.main);
        // Look up the AdView as a resource and load a request.
        AdView adView = (AdView) this.findViewById(R.id.adView);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("ads", true)) {
            adView.setVisibility(View.VISIBLE);
            Bundle bundle = new Bundle();
            bundle.putString("color_bg", "4285f4");
            bundle.putString("color_bg_top", "4285f4");
            bundle.putString("color_border", "4285f4");
            bundle.putString("color_link", "EEEEEE");
            bundle.putString("color_text", "FFFFFF");
            bundle.putString("color_url", "EEEEEE");
            AdMobExtras extras = new AdMobExtras(bundle);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("E9439BFF2245E1BC1DD0FDB28EA467F9").addTestDevice("49924C4BF3738C69A7497A524D092901").addNetworkExtras(extras).build();
            adView.loadAd(adRequest);
        } else {
            adView.setVisibility(View.GONE);
        }
        // Create new fragment and transaction

        SettingsFragment newFragment = new SettingsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack

        transaction.add(R.id.fragment_container, newFragment);

        // Commit the transaction
        transaction.commit();

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(this);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            boolean ranApptentive = Apptentive.handleOpenedPushNotification(this);
            Apptentive.engage(this, "init");
            if (ranApptentive) {
                // Don't try to take any action here. Wait until the customer closes our UI.
                return;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.support:
                Apptentive.showMessageCenter(this, getSharedPreferences(this));
                break;
            case R.id.setup:
                Intent i = new Intent(this, SetupActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.share:
                List<Intent> targetedShareIntents = new ArrayList<Intent>();
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
                if (!resInfo.isEmpty()) {
                    for (ResolveInfo resolveInfo : resInfo) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        targetedShareIntent.setType("text/plain");
                        targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        if (packageName.equals("com.facebook.katana")) {
                            targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://Commandr.RSenApps.com");
                        } else {
                            targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.fb_share_msg));
                        }

                        targetedShareIntent.setPackage(packageName);
                        targetedShareIntents.add(targetedShareIntent);


                    }
                    Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");

                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));

                    startActivity(chooserIntent);

                }
                break;
            case R.id.about:
                new MaterialDialog.Builder(this)
                        .title(R.string.about)
                        .content(R.string.about_message)
                        .callback(new MaterialDialog.SimpleCallback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                materialDialog.dismiss();
                            }
                        })
                        .build()
                        .show();

                break;
        }
        return true;
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {

            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private void registerInBackground() {
        new AsyncTask() {

            @Override
            protected String doInBackground(Object[] objects) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    Apptentive.addAmazonSnsPushIntegration(MainActivity.this, regid);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(MainActivity.this, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


}
