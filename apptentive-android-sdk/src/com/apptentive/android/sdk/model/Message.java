/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.model;

import com.apptentive.android.sdk.GlobalInfo;
import com.apptentive.android.sdk.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author Sky Kelsey
 */
public abstract class Message extends ConversationItem {

    public static final String KEY_ID = "id";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_TYPE = "type";
    public static final String KEY_HIDDEN = "hidden";
    public static final String KEY_CUSTOM_DATA = "custom_data";
    private static final String KEY_SENDER = "sender";
    private static final String KEY_SENDER_ID = "id";
    private static final String KEY_SENDER_NAME = "name";
    private static final String KEY_SENDER_PROFILE_PHOTO = "profile_photo";
    // State and Read are not stored in JSON, only in DB.
    private State state;
    private boolean read = false;

    protected Message() {
        super();
        setSenderId(GlobalInfo.personId);
        state = State.sending;
        read = true; // This message originated here.
        setBaseType(BaseType.message);
        initType();
    }

    protected Message(String json) throws JSONException {
        super(json);
    }

    protected void initBaseType() {
        setBaseType(BaseType.message);
    }

    protected abstract void initType();

    public String getId() {
        try {
            if (!isNull((KEY_ID))) {
                return getString(KEY_ID);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setId(String id) {
        try {
            put(KEY_ID, id);
        } catch (JSONException e) {
            Log.e("Exception setting Message's %s field.", e, KEY_ID);
        }
    }

    public Double getCreatedAt() {
        try {
            return getDouble(KEY_CREATED_AT);
        } catch (JSONException e) {
        }
        return null;
    }

    public void setCreatedAt(Double createdAt) {
        try {
            put(KEY_CREATED_AT, createdAt);
        } catch (JSONException e) {
            Log.e("Exception setting Message's %s field.", e, KEY_CREATED_AT);
        }
    }

    public Type getType() {
        try {
            return Type.parse(getString(KEY_TYPE));
        } catch (JSONException e) {
        }
        return Type.unknown;
    }

    protected void setType(Type type) {
        try {
            put(KEY_TYPE, type.name());
        } catch (JSONException e) {
            Log.e("Exception setting Message's %s field.", e, KEY_TYPE);
        }
    }

    public boolean isHidden() {
        try {
            return getBoolean(KEY_HIDDEN);
        } catch (JSONException e) {
        }
        return false;
    }

    public void setHidden(boolean hidden) {
        try {
            put(KEY_HIDDEN, hidden);
        } catch (JSONException e) {
            Log.e("Exception setting Message's %s field.", e, KEY_HIDDEN);
        }
    }

    public void setCustomData(Map<String, String> customData) {
        if (customData == null || customData.size() == 0) {
            if (!isNull(KEY_CUSTOM_DATA)) {
                remove(KEY_CUSTOM_DATA);
            }
            return;
        }
        try {
            JSONObject customDataJson = new JSONObject();
            for (String key : customData.keySet()) {
                customDataJson.put(key, customData.get(key));
            }
            put(KEY_CUSTOM_DATA, customDataJson);
        } catch (JSONException e) {
            Log.e("Exception setting Message's %s field.", e, KEY_CUSTOM_DATA);
        }
    }

    public State getState() {
        if (state == null) {
            return State.unknown;
        }
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getSenderId() {
        try {
            if (!isNull((KEY_SENDER))) {
                JSONObject sender = getJSONObject(KEY_SENDER);
                if (!sender.isNull((KEY_SENDER_ID))) {
                    return sender.getString(KEY_SENDER_ID);
                }
            }
        } catch (JSONException e) {
        }
        return null;
    }

    // For debugging only.
    public void setSenderId(String senderId) {
        try {
            JSONObject sender;
            if (!isNull((KEY_SENDER))) {
                sender = getJSONObject(KEY_SENDER);
            } else {
                sender = new JSONObject();
                put(KEY_SENDER, sender);
            }
            sender.put(KEY_SENDER_ID, senderId);
        } catch (JSONException e) {
            Log.e("Exception setting Message's %s field.", e, KEY_SENDER_ID);
        }
    }

    public String getSenderUsername() {
        try {
            if (!isNull((KEY_SENDER))) {
                JSONObject sender = getJSONObject(KEY_SENDER);
                if (!sender.isNull((KEY_SENDER_NAME))) {
                    return sender.getString(KEY_SENDER_NAME);
                }
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public String getSenderProfilePhoto() {
        try {
            if (!isNull((KEY_SENDER))) {
                JSONObject sender = getJSONObject(KEY_SENDER);
                if (!sender.isNull((KEY_SENDER_PROFILE_PHOTO))) {
                    return sender.getString(KEY_SENDER_PROFILE_PHOTO);
                }
            }
        } catch (JSONException e) {
            // Should not happen.
        }
        return null;
    }

    public boolean isOutgoingMessage() {
        String senderId = getSenderId();
        boolean outgoing = senderId == null || senderId.equals(GlobalInfo.personId) || getState().equals(State.sending);
        return outgoing;
    }

    public enum Type {
        TextMessage,
        FileMessage,
        AutomatedMessage,

        // Unknown
        unknown;

        public static Type parse(String rawType) {
            try {
                return Type.valueOf(rawType);
            } catch (IllegalArgumentException e) {
                Log.v("Error parsing unknown Message.Type: " + rawType);
            }
            return unknown;
        }
    }

    public static enum State {
        sending, // The item is either being sent, or is queued for sending.
        sent,    // The item has been posted to the server successfully.
        saved,   // The item has been returned from the server during a fetch.
        unknown;

        public static State parse(String state) {
            try {
                return State.valueOf(state);
            } catch (IllegalArgumentException e) {
                Log.v("Error parsing unknown Message.State: " + state);
            }
            return unknown;
        }
    }
}
