/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.model;

import org.json.JSONException;

/**
 * @author Sky Kelsey
 */
public class UpgradeMessageInteraction extends Interaction {
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_APP_VERSION = "app_version";
    private static final String KEY_SHOW_APP_ICON = "show_app_icon";
    private static final String KEY_SHOW_POWERED_BY = "show_powered_by";
    private static final String KEY_BODY = "body";

    public UpgradeMessageInteraction(String json) throws JSONException {
        super(json);
    }

    public boolean isActive() {
        try {
            InteractionConfiguration configuration = getConfiguration();
            if (configuration != null && configuration.has(KEY_ACTIVE)) {
                return configuration.getBoolean(KEY_ACTIVE);
            }
        } catch (JSONException e) {
        }
        return false;
    }

    public String getAppVersion() {
        try {
            InteractionConfiguration configuration = getConfiguration();
            if (configuration != null && configuration.has(KEY_APP_VERSION)) {
                return configuration.getString(KEY_APP_VERSION);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public boolean isShowAppIcon() {
        try {
            InteractionConfiguration configuration = getConfiguration();
            if (configuration != null && configuration.has(KEY_SHOW_APP_ICON)) {
                return configuration.getBoolean(KEY_SHOW_APP_ICON);
            }
        } catch (JSONException e) {
        }
        return false;
    }

    public boolean isShowPoweredBy() {
        try {
            InteractionConfiguration configuration = getConfiguration();
            if (configuration != null && configuration.has(KEY_SHOW_POWERED_BY)) {
                return configuration.getBoolean(KEY_SHOW_POWERED_BY);
            }
        } catch (JSONException e) {
        }
        return false;
    }

    public String getBody() {
        try {
            InteractionConfiguration configuration = getConfiguration();
            if (configuration != null && configuration.has(KEY_BODY)) {
                return configuration.getString(KEY_BODY);
            }
        } catch (JSONException e) {
        }
        return null;
    }


}
