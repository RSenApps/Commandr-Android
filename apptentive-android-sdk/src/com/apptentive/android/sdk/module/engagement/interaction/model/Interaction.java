/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.model;

import android.content.Context;

import com.apptentive.android.sdk.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Sky Kelsey
 */
public abstract class Interaction extends JSONObject implements Comparable {

    public static final String KEY_NAME = "interaction";

    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_VERSION = "version";
    private static final String KEY_PRIORITY = "priority";
    private static final String KEY_CRITERIA = "criteria";
    private static final String KEY_CONFIGURATION = "configuration";

    public Interaction(String json) throws JSONException {
        super(json);
    }

    /**
     * Override this method if the subclass has certain restrictions other than Criteria that it needs to evaluate in
     * order to know if it can run. Example: Interactions that will require an internet connection must override and check
     * for network availability.
     *
     * @param context The Context from which this method is called.
     * @return true if this interaction can run.
     */
    protected boolean isInRunnableState(Context context) {
        return true;
    }

    /**
     * An interaction can run if local state is conducive to it running, and criteria is met. Interactions that are
     * missing criteria, or where criteria is null cannot run. There must at least be a criteria object on an
     * interaction in order for it to run.
     *
     * @return true iff the interaction can be run.
     */
    public boolean canRun(Context context) {
        InteractionCriteria criteria = getCriteria();
        Log.v("=== Checking interaction %s ===", getType());
        return criteria != null && isInRunnableState(context) && criteria.isMet(context);
    }

    public String getId() {
        try {
            if (!isNull(KEY_ID)) {
                return getString(KEY_ID);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public Type getType() {
        try {
            if (!isNull(KEY_TYPE)) {
                return Type.parse(getString(KEY_TYPE));
            }
        } catch (JSONException e) {
        }
        return Type.unknown;
    }

    public Integer getVersion() {
        try {
            if (!isNull(KEY_VERSION)) {
                return getInt(KEY_VERSION);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public Integer getPriority() {
        try {
            if (!isNull(KEY_PRIORITY)) {
                return getInt(KEY_PRIORITY);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public InteractionCriteria getCriteria() {
        try {
            if (!isNull(KEY_CRITERIA)) {
                return new InteractionCriteria(getJSONObject(KEY_CRITERIA).toString());
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public InteractionConfiguration getConfiguration() {
        try {
            if (!isNull(KEY_CONFIGURATION)) {
                return new InteractionConfiguration(getJSONObject(KEY_CONFIGURATION).toString());
            }
        } catch (JSONException e) {
        }
        return new InteractionConfiguration();
    }

    /**
     * Compares two Interactions based on priority. Lower values are considered higher priority.
     *
     * @param interaction The other Interaction to compare to this Interaction.
     * @return -1 if this Interaction is higher priority than the passed Interaction.<p/>
     * 0 if this Interaction is of equal priority to the passed Interaction.<p/>
     * 1 if this Interaction is of lower priority than the passed Interaction.<p/>
     */
    @Override
    public int compareTo(Object interaction) {
        return getPriority().compareTo(((Interaction) interaction).getPriority());
    }

    public static enum Type {
        UpgradeMessage,
        EnjoymentDialog,
        RatingDialog,
        FeedbackDialog,
        MessageCenter,
        AppStoreRating,
        Survey,
        unknown;

        public static Type parse(String type) {
            try {
                return Type.valueOf(type);
            } catch (IllegalArgumentException e) {
                Log.v("Error parsing unknown Interaction.Type: " + type);
            }
            return unknown;
        }
    }

    public static class Factory {
        public static Interaction parseInteraction(String interactionString) {
            try {
                Interaction.Type type = Type.unknown;
                JSONObject interaction = new JSONObject(interactionString);
                if (interaction.has(KEY_TYPE)) {
                    type = Type.parse(interaction.getString(KEY_TYPE));
                }
                switch (type) {
                    case UpgradeMessage:
                        return new UpgradeMessageInteraction(interactionString);
                    case EnjoymentDialog:
                        return new EnjoymentDialogInteraction(interactionString);
                    case RatingDialog:
                        return new RatingDialogInteraction(interactionString);
                    case FeedbackDialog:
                        return new FeedbackDialogInteraction(interactionString);
                    case MessageCenter:
                        return new MessageCenterInteraction(interactionString);
                    case AppStoreRating:
                        return new AppStoreRatingInteraction(interactionString);
                    case Survey:
                        return new SurveyInteraction(interactionString);
                    case unknown:
                        break;
                }
            } catch (JSONException e) {
            }
            return null;
        }
    }
}
