/*
 * Copyright (C) 2011-2014 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.RSen.Commandr.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.RSen.Commandr.BuildConfig;
import com.RSen.Commandr.R;

import org.sufficientlysecure.donations.DonationsFragment;

public class DonationsActivity extends ActionBarActivity {

    /**
     * Google
     */
    private static final String GOOGLE_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlUs6lO22nKayvjdm8qDJGXzNRzPeZFLoMSzBGSESwIN/pSBmKKt8JRlZGGyvMrHvmvoojXpi0T+jse7lCttJsP/ZqM9RD4h+Aq4R/tuPpUMDkTUuHzoB/cRr9MN16rVdEQF2/XxVIfKAQqAsb8DUXWFbKoDgUtGicNXXlNvgMN8dcyQJWB2vPZ1MRNz7cw57JnLZwZLYKhxps1qvmYVrMYaizY1m7acfG11HRkAWEPymxrrMN39eIBCJt2XNNLQYBp+rCMoId1x05AK3FTDYsGTdulCHGW55a8KiUszrIe8a4hfU1DeZkkPam7LhY9qoF2TR0nZl9rRb+xtDcaHGZQIDAQAB";
    private static final String[] GOOGLE_CATALOG = new String[]{"commandr.donation.1",
            "commandr.donation.2", "commandr.donation.3", "commandr.donation.5", "commandr.donation.8",
            "commandr.donation.10", "commandr.donation.15", "commandr.donation.20", "commandr.donation.25", "commandr.donation.50", "commandr.donation.100"};

    /**
     * PayPal
     */
    private static final String PAYPAL_USER = "rsenapps@gmail.com";
    private static final String PAYPAL_CURRENCY_CODE = "USD";

    /**
     * Flattr
     */
    private static final String FLATTR_PROJECT_URL = "http://Commandr.RSenApps.com";
    // FLATTR_URL without http:// !
    private static final String FLATTR_URL = "flattr.com/thing/ae74b398bee9762040260d015b7035a6";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.donations_activity);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DonationsFragment donationsFragment;
        if (BuildConfig.DONATIONS_GOOGLE) {
            donationsFragment = DonationsFragment.newInstance(BuildConfig.DEBUG, true, GOOGLE_PUBKEY, GOOGLE_CATALOG,
                    getResources().getStringArray(R.array.donation_google_catalog_values), true, PAYPAL_USER, PAYPAL_CURRENCY_CODE,
                    getString(R.string.donation_paypal_item), true, FLATTR_PROJECT_URL, FLATTR_URL, false, null);
        } else {
            donationsFragment = DonationsFragment.newInstance(BuildConfig.DEBUG, false, null, null, null, true, PAYPAL_USER,
                    PAYPAL_CURRENCY_CODE, getString(R.string.donation_paypal_item), true, FLATTR_PROJECT_URL, FLATTR_URL, false, null);
        }

        ft.replace(R.id.donations_activity_container, donationsFragment, "donationsFragment");
        ft.commit();
    }

    /**
     * Needed for Google Play In-app Billing. It uses startIntentSenderForResult(). The result is not propagated to
     * the Fragment like in startActivityForResult(). Thus we need to propagate manually to our Fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("donationsFragment");
        setupActionBar();
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
