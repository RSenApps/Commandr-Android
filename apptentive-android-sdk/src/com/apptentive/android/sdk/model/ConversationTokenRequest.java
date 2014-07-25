/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.model;

import com.apptentive.android.sdk.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Sky Kelsey
 */
public class ConversationTokenRequest extends JSONObject {


    public ConversationTokenRequest() {
    }

    public void setDevice(Device device) {
        try {
            put(Device.KEY, device);
        } catch (JSONException e) {
            Log.e("Error adding %s to ConversationTokenRequest", Device.KEY);
        }
    }

    public void setSdk(Sdk sdk) {
        try {
            put(Sdk.KEY, sdk);
        } catch (JSONException e) {
            Log.e("Error adding %s to ConversationTokenRequest", Sdk.KEY);
        }
    }

    public void setPerson(Person person) {
        try {
            put(Person.KEY, person);
        } catch (JSONException e) {
            Log.e("Error adding %s to ConversationTokenRequest", Person.KEY);
        }
    }

    //TODO: Handle client info as well.
}
