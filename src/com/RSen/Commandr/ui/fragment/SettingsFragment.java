package com.RSen.Commandr.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MyAccessibilityService;
import com.RSen.Commandr.tasker.TaskerIntent;
import com.RSen.Commandr.ui.activity.MostWantedCommandsActivity;
import com.RSen.Commandr.ui.activity.SetupActivity;
import com.RSen.Commandr.ui.activity.TaskerActivity;


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
