/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
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
public class MessageFactory {
    public static Message fromJson(String json) {
        try {
            JSONObject root = new JSONObject(json);
            Message.Type type = Message.Type.valueOf(root.getString(Message.KEY_TYPE));
            switch (type) {
                case TextMessage:
                    return new TextMessage(json);
                case FileMessage:
                    return new FileMessage(json);
                case AutomatedMessage:
                    return new AutomatedMessage(json);
                case unknown:
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            Log.v("Error parsing json as Message: %s", e, json);
        } catch (IllegalArgumentException e) {
            // Unknown unknown #rumsfeld
        }
        return null;
    }
}
