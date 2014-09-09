package com.RSen.Commandr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         NoteToSelfActivity.java
 * @version 1.0
 *          5/28/14
 */
public class NoteToSelfActivity extends Activity {
    /**
     * Caled when the activity is created... run the intercepted command
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String interceptedCommand = getIntent().getExtras().getString(Intent.EXTRA_TEXT); //command phrase sent by Gooogle Now
            if (interceptedCommand != null) {
                WearUtil.sendCommandMessage(this, interceptedCommand, false);
            }
            else {
                finish();
            }
        } catch (Exception e) {
            finish();
        }

    }



}
