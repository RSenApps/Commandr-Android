/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.model;

import com.apptentive.android.sdk.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sky Kelsey
 */
public class Interactions extends JSONObject {
    private static final String KEY_INTERACTIONS = "interactions";

    public Interactions(String json) throws JSONException {
        super(json);
    }

    public List<Interaction> getInteractionList(String codePoint) {
        List<Interaction> list = new ArrayList<Interaction>();
        try {
            JSONObject interactions = getJSONObject(KEY_INTERACTIONS);
            if (!interactions.isNull(codePoint)) {
                JSONArray interactionsForCodePoint = interactions.getJSONArray(codePoint);
                for (int i = 0; i < interactionsForCodePoint.length(); i++) {
                    String interactionString = interactionsForCodePoint.getJSONObject(i).toString();
                    Interaction interaction = Interaction.Factory.parseInteraction(interactionString);
                    if (interaction != null) {
                        list.add(interaction);
                    }
                }
            }
        } catch (JSONException e) {
            Log.w("Exception parsing interactions array.", e);
        }
        // Sort list by Priority
        Collections.sort(list);
        return list;
    }
}
