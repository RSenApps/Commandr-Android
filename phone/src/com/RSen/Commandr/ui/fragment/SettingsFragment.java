package com.RSen.Commandr.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MyAccessibilityService;
import com.RSen.Commandr.tasker.TaskerIntent;
import com.RSen.Commandr.ui.activity.DonationsActivity;
import com.RSen.Commandr.ui.activity.MostWantedCommandsActivity;
import com.RSen.Commandr.ui.activity.SetupActivity;
import com.RSen.Commandr.ui.activity.TaskerActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;


/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         SettingsFragment.java
 * @version 1.0
 *          5/28/14
 */
public class SettingsFragment extends PreferenceFragment {
    /**
     * Called when the fragment is created
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        findPreference("mostWanted").setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference arg0) {
                // Create new fragment and transaction
                startActivity(new Intent(getActivity(), MostWantedCommandsActivity.class));
                return true;
            }
        });
        findPreference("donate").setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference arg0) {
                // Create new fragment and transaction
                startActivity(new Intent(getActivity(), DonationsActivity.class));
                return true;
            }
        });
        findPreference("usexposed").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                PackageManager pm = getActivity().getPackageManager();
                boolean app_installed = false;
                try {
                    pm.getPackageInfo("com.mohammadag.googlesearchapi", PackageManager.GET_ACTIVITIES);
                    app_installed = true;
                }
                catch (PackageManager.NameNotFoundException e) {
                    app_installed = false;
                }
                if (app_installed)
                {
                    if (MyAccessibilityService.isAccessibilitySettingsOn(getActivity())) {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.disable_accessibility), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivityForResult(intent, 2);
                    }
                    return true;
                }
                else {
                    Toast.makeText(getActivity(), "Please first install the Xposed Module...", Toast.LENGTH_LONG).show();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://forum.xda-developers.com/xposed/modules/mod-google-search-api-t2554173"));
                    startActivity(browserIntent);
                    return false;
                }
            }
        });
        findPreference("ads").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                AdView adView = (AdView) getActivity().findViewById(R.id.adView);
                if ((Boolean) o) {
                    adView.setVisibility(View.VISIBLE);
                    Bundle bundle = new Bundle();
                    bundle.putString("color_bg", "4285f4");
                    bundle.putString("color_bg_top", "4285f4");
                    bundle.putString("color_border", "4285f4");
                    bundle.putString("color_link", "EEEEEE");
                    bundle.putString("color_text", "FFFFFF");
                    bundle.putString("color_url", "EEEEEE");
                    AdMobExtras extras = new AdMobExtras(bundle);
                    AdRequest adRequest = new AdRequest.Builder().addTestDevice("49924C4BF3738C69A7497A524D092901").addNetworkExtras(extras).build();
                    adView.loadAd(adRequest);
                } else {
                    adView.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            boolean noteToSelfRequired = !MyAccessibilityService.isAccessibilitySettingsOn(getActivity());
            if (noteToSelfRequired) {
                ((CheckBoxPreference) findPreference("notetoselfrequired")).setChecked(true);
                findPreference("notetoselfrequired").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        ((CheckBoxPreference) preference).setChecked(!((Boolean) o));
                        Toast.makeText(getActivity(), R.string.change_reconfigure, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getActivity(), SetupActivity.class));
                        getActivity().finish();
                        return false;
                    }
                });
            } else {
                getPreferenceScreen().removePreference(findPreference("notetoselfrequired"));
            }
        } catch (Exception e) {

        }

        if (TaskerIntent.taskerInstalled(getActivity())) {
            if (TaskerIntent.testStatus(getActivity()).equals(TaskerIntent.Status.NotEnabled)) {
                findPreference("taskerCommands").setSummary(getString(R.string.enable_tasker));
            } else if (TaskerIntent.testStatus(getActivity()).equals(TaskerIntent.Status.AccessBlocked)) {
                findPreference("taskerCommands").setSummary(getString(R.string.tasker_permission));
                findPreference("taskerCommands").setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        startActivity(TaskerIntent.getExternalAccessPrefsIntent());
                        return true;
                    }
                });
            } else {
                findPreference("taskerCommands").setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        // Create new fragment and transaction
                        startActivity(new Intent(getActivity(), TaskerActivity.class));
                        return true;
                    }
                });
            }
        } else {
            findPreference("taskerCommands").setSummary(getString(R.string.install_tasker));
            findPreference("taskerCommands").setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {

                    final String appPackageName = "net.dinglisch.android.taskerm"; // getPackageName() from Context or Activity object
                    try {
                        getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    return true;
                }
            });
        }
    }
}
