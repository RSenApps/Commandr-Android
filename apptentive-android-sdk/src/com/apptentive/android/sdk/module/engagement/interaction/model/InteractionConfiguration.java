package com.apptentive.android.sdk.module.engagement.interaction.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Sky Kelsey
 */
public class InteractionConfiguration extends JSONObject {

    private static final String KEY_SHOW_POWERED_BY = "show_powered_by";

    public InteractionConfiguration() {
        super();
    }

    public InteractionConfiguration(String json) throws JSONException {
        super(json);
    }

    public boolean isShowPoweredBy() {
        try {
            if (!isNull(KEY_SHOW_POWERED_BY)) {
                return getBoolean(KEY_SHOW_POWERED_BY);
            }
        } catch (JSONException e) {
        }
        return false;
    }
}
