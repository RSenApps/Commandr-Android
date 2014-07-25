package com.RSen.Commandr.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MyAccessibilityService;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

public class SetupActivity extends Activity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint

            tintManager.setTintResource(android.R.color.background_light);

        }
        setContentView(R.layout.activity_setup);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public static class WelcomeFragment extends Fragment {


        public WelcomeFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.setup_welcome, container, false);
            return rootView;
        }
    }

    public static class AccessibilityFragment extends Fragment {
        public AccessibilityFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.setup_accessibility, container, false);
            final TextView instructions = (TextView) rootView.findViewById(R.id.tV2);
            final Button openSettings = (Button) rootView.findViewById(R.id.openSettings);
            final Button limited = (Button) rootView.findViewById(R.id.limited);
            final Button done = (Button) rootView.findViewById(R.id.done);

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("setup", true).commit();
                    Intent main = new Intent(getActivity(), MainActivity.class);
                    startActivity(main);
                    getActivity().finish();
                }
            });
            if (MyAccessibilityService.isAccessibilitySettingsOn(getActivity())) {
                instructions.setText(getString(R.string.accessibility_already_configured));
                openSettings.setVisibility(View.GONE);
                limited.setVisibility(View.GONE);
                done.setVisibility(View.VISIBLE);
            }
            openSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivityForResult(intent, 2);
                }
            });
            limited.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openSettings.setVisibility(View.GONE);
                    limited.setVisibility(View.GONE);
                    instructions.setText(R.string.swipe_left);
                    if (((SetupActivity) getActivity()).mSectionsPagerAdapter.getCount() < 3) {
                        ((SetupActivity) getActivity()).mSectionsPagerAdapter.addItem(new IntentFragment());
                        ((SetupActivity) getActivity()).mSectionsPagerAdapter.addItem(new TestFragment());
                    }
                }
            });
            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 2) {
                View rootView = getView();
                final TextView instructions = (TextView) rootView.findViewById(R.id.tV2);
                final Button openSettings = (Button) rootView.findViewById(R.id.openSettings);
                final Button limited = (Button) rootView.findViewById(R.id.limited);
                final Button done = (Button) rootView.findViewById(R.id.done);
                if (MyAccessibilityService.isAccessibilitySettingsOn(getActivity())) {

                    instructions.setText(getString(R.string.accessibility_configured));
                    openSettings.setVisibility(View.GONE);
                    limited.setVisibility(View.GONE);
                    done.setVisibility(View.VISIBLE);
                    ((SetupActivity) getActivity()).mSectionsPagerAdapter.addItem(new PassthroughFragment());

                }
            }
        }
    }

    public static class IntentFragment extends Fragment {


        public IntentFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.setup_intent, container, false);
            final TextView instructions = (TextView) rootView.findViewById(R.id.tV2);
            final Button openDialog = (Button) rootView.findViewById(R.id.openDialog);
            final View divider = rootView.findViewById(R.id.divider);
            final TextView swipeLeft = (TextView) rootView.findViewById(R.id.swipeLeft);
            if (checkIfDefault()) {
                instructions.setText(getString(R.string.default_already_set));
                openDialog.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                swipeLeft.setVisibility(View.VISIBLE);
            } else {
                Intent i = new Intent("com.google.android.gm.action.AUTO_SEND");
                i.putExtra(Intent.EXTRA_TEXT, "TEST");
                i.setType("text/plain");
                PackageManager pm = getActivity().getPackageManager();
                List<ResolveInfo> resInfo = pm.queryIntentActivities(i, 0);
                int count = 0;
                for (ResolveInfo info : resInfo) {
                    if (info.activityInfo.permission == null) {
                        count++;
                    }
                }
                if (count < 2) {
                    instructions.setText(getString(R.string.dialog_hidden));
                    openDialog.setVisibility(View.GONE);
                    divider.setVisibility(View.GONE);
                    swipeLeft.setVisibility(View.VISIBLE);
                }
            }
            openDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent("com.google.android.gm.action.AUTO_SEND");
                    i.putExtra(Intent.EXTRA_TEXT, "TEST");
                    i.setType("text/plain");
                    try {
                        startActivityForResult(i, 1);
                    } catch (Exception e) {
                        instructions.setText(getString(R.string.dialog_hidden));
                        openDialog.setVisibility(View.GONE);
                        divider.setVisibility(View.GONE);
                        swipeLeft.setVisibility(View.VISIBLE);
                    }
                }
            });
            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 1) {
                View rootView = getView();
                TextView instructions = (TextView) rootView.findViewById(R.id.tV2);
                Button openDialog = (Button) rootView.findViewById(R.id.openDialog);
                TextView swipeLeft = (TextView) rootView.findViewById(R.id.swipeLeft);
                View divider = rootView.findViewById(R.id.divider);
                if (checkIfDefault()) {

                    instructions.setText(getString(R.string.default_set_successfully));
                    openDialog.setVisibility(View.GONE);
                    swipeLeft.setVisibility(View.VISIBLE);
                    divider.setVisibility(View.GONE);
                }
            }
        }

        private boolean checkIfDefault() {

            Intent i = new Intent("com.google.android.gm.action.AUTO_SEND");
            i.putExtra(Intent.EXTRA_TEXT, "TEST");
            i.setType("text/plain");

            PackageManager pm = getActivity().getPackageManager();
            List<ResolveInfo> resInfo = pm.queryIntentActivities(i, 0);
            if (resInfo.size() < 2) {
                return true;
            }
            final ResolveInfo mInfo = pm.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);
            try {

                return mInfo.activityInfo.packageName.equals("com.RSen.Commandr");
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static class TestFragment extends Fragment {


        public TestFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.setup_test, container, false);
            final TextView swipeLeft = (TextView) rootView.findViewById(R.id.swipeLeft);
            final EditText testInput = (EditText) rootView.findViewById(R.id.testInput);
            testInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().toLowerCase().trim().equals(getString(R.string.test_correct_answer))) {
                        testInput.setBackgroundColor(Color.GREEN);
                        if (swipeLeft.getVisibility() != View.VISIBLE) {
                            swipeLeft.setVisibility(View.VISIBLE);
                            ((SetupActivity) getActivity()).mSectionsPagerAdapter.addItem(new PassthroughFragment());
                        }
                    } else {
                        testInput.setBackgroundColor(Color.RED);
                        if (editable.toString().toLowerCase().trim().equals("pause music")) {
                            testInput.setError(getString(R.string.reminder_note_to_self));
                        }
                        if (editable.toString().contains(",") || editable.toString().contains("\"") || editable.toString().contains(".")) {
                            testInput.setError(getString(R.string.no_punctuation));
                        }
                    }
                }
            });
            return rootView;
        }
    }

    public static class PassthroughFragment extends Fragment {


        public PassthroughFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.setup_passthrough, container, false);
            Intent i = new Intent("com.google.android.gm.action.AUTO_SEND");
            i.putExtra(Intent.EXTRA_TEXT, "TEST");
            i.setType("text/plain");
            List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);

            if (resInfo.size() < 2) {
                ListView listView = ((ListView) rootView.findViewById(R.id.listView));
                listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new String[]{"No note-taking apps found..."}));
                rootView.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("setup", true).commit();
                        Intent main = new Intent(getActivity(), MainActivity.class);
                        startActivity(main);
                        getActivity().finish();
                    }
                });
            } else {
                ResolveInfo[] resInfoArray = new ResolveInfo[resInfo.size() - 1];
                int index = 0;
                for (ResolveInfo resolveInfo : resInfo) {
                    if (!resolveInfo.activityInfo.packageName.equals(getActivity().getPackageName())) {
                        resInfoArray[index] = resolveInfo;
                        index++;
                    }
                }
                final ResolveInfoAdapter adapter = new ResolveInfoAdapter(getActivity(), resInfoArray);
                ListView listView = ((ListView) rootView.findViewById(R.id.listView));
                listView.setAdapter(adapter);
                rootView.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("setup", true).putString("passthrough_pkg", adapter.getSelectedPackageName()).commit();
                        Intent main = new Intent(getActivity(), MainActivity.class);
                        startActivity(main);
                        getActivity().finish();
                    }
                });
            }

            return rootView;
        }

        public static class ResolveInfoAdapter extends ArrayAdapter<ResolveInfo> {
            private final Context context;
            private final ResolveInfo[] values;
            private int selectedPosition = 0;

            public ResolveInfoAdapter(Context context, ResolveInfo[] values) {
                super(context, R.layout.setup_passthrough_row, values);
                this.context = context;
                this.values = values;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.setup_passthrough_row, parent, false);
                }
                TextView tv = (TextView) convertView.findViewById(R.id.tV);
                RadioButton radioButton = (RadioButton) convertView.findViewById(R.id.radioButton);
                radioButton.setChecked(position == selectedPosition);
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b && selectedPosition != position) {
                            selectedPosition = position;
                            notifyDataSetChanged();
                        }
                    }
                });
                ResolveInfo info = values[position];
                tv.setText(info.activityInfo.applicationInfo.loadLabel(context.getPackageManager()));
                Resources r = context.getResources();
                int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
                Drawable icon = info.loadIcon(context.getPackageManager());
                icon.setBounds(0, 0, px, px);

                tv.setCompoundDrawables(icon, null, null, null);
                return convertView;
            }

            public String getSelectedPackageName() {
                return values[selectedPosition].activityInfo.packageName;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments.add(new WelcomeFragment());
            fragments.add(new AccessibilityFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addItem(Fragment fragment) {
            fragments.add(fragment);
            notifyDataSetChanged();
        }

    }

}
