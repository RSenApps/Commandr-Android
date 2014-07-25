/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.model;

import com.apptentive.android.sdk.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sky Kelsey
 */
public class Event extends ConversationItem {

    private static final String KEY_LABEL = "label";
    private static final String KEY_DATA = "data";
    private static final String KEY_TRIGGER = "trigger";

    public Event(String json) throws JSONException {
        super(json);
    }

    public Event(String label, Map<String, String> data) {
        super();
        try {
            put(KEY_LABEL, label);
            if (data != null && !data.isEmpty()) {
                JSONObject dataObject = new JSONObject();
                for (String key : data.keySet()) {
                    dataObject.put(key, data.get(key));
                }
                put(KEY_DATA, dataObject);
            }
        } catch (JSONException e) {
            Log.e("Unable to construct Event.", e);
        }
    }

    public Event(String label, JSONObject data) {
        super();
        try {
            put(KEY_LABEL, label);
            if (data != null) {
                put(KEY_DATA, data);
            }
        } catch (JSONException e) {
            Log.e("Unable to construct Event.", e);
        }
    }

    public Event(String label, String trigger) {
        this(label, (Map<String, String>) null);
        Map<String, String> data = new HashMap<String, String>();
        data.put(KEY_TRIGGER, trigger);
        putData(data);
    }

    @Override
    protected void initBaseType() {
        setBaseType(BaseType.event);
    }

    public void putData(Map<String, String> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        try {
            JSONObject dataObject;
            if (isNull(KEY_DATA)) {
                dataObject = new JSONObject();
                put(KEY_DATA, dataObject);
            } else {
                dataObject = getJSONObject(KEY_DATA);
            }
            for (String key : data.keySet()) {
                dataObject.put(key, data.get(key));
            }
        } catch (JSONException e) {
            Log.e("Unable to add data to Event.", e);
        }
    }

    public static enum EventLabel {

        app__launch("launch"),
        app__exit("exit"),

        message_center__launch("message_center.launch"),
        message_center__close("message_center.close"),
        message_center__attach("message_center.attach"),
        message_center__read("message_center.read"),

        message_center__intro__launch("message_center.intro.launch"),
        message_center__intro__send("message_center.intro.send"),
        message_center__intro__cancel("message_center.intro.cancel"),

        message_center__thank_you__launch("message_center.thank_you.launch"),
        message_center__thank_you__messages("message_center.thank_you.messages"),
        message_center__thank_you__close("message_center.thank_you.close"),

        error("error");

        private final String labelName;

        private EventLabel(String labelName) {
            this.labelName = labelName;
        }

        public String getLabelName() {
            return labelName;
        }
    }
}