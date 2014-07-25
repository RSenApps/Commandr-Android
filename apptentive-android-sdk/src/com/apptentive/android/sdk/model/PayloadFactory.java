/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.model;

import com.apptentive.android.sdk.Log;

import org.json.JSONException;

/**
 * @author Sky Kelsey
 */
public class PayloadFactory {

    public static Payload fromJson(String json, Payload.BaseType baseType) {
        switch (baseType) {
            case message:
                return MessageFactory.fromJson(json);
            case event:
                return EventFactory.fromJson(json);
            case device:
                return DeviceFactory.fromJson(json);
            case sdk:
                return SdkFactory.fromJson(json);
            case app_release:
                return AppReleaseFactory.fromJson(json);
            case person:
                return PersonFactory.fromJson(json);
            case survey:
                try {
                    return new SurveyResponse(json);
                } catch (JSONException e) {
                }
            case unknown:
                Log.v("Ignoring unknown RecordType.");
                break;
            default:
                break;
        }
        return null;
    }
}
