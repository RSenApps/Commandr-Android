/*
 * Copyright (c) 2012, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;

/**
 * Extend this class instead of Activity to easily integrate Apptentive into your application.
 * <p/>
 * If you are unable to inherit from our Activity, you can delegate the calls to Apptentive static methods into your
 * own Activity manually, as specified below.
 * <p/>
 * All Activities in your Application MUST integrate Apptentive by extending of of the Apptentive Activities, or by
 * delegation.
 *
 * @author Sky Kelsey
 * @see ApptentiveListActivity
 */
public class ApptentiveActivity extends ActionBarActivity {

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
}
