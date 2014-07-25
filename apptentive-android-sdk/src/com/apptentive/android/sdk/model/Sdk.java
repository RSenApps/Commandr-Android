/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.model;

import com.apptentive.android.sdk.Log;

import org.json.JSONException;

/**
 * @author Sky Kelsey
 */
public class Sdk extends Payload {

    public static final String KEY = "sdk";

    private static final String KEY_VERSION = "version";
    private static final String KEY_PROGRAMMING_LANGUAGE = "programming_language";
    private static final String KEY_AUTHOR_NAME = "author_name";
    private static final String KEY_AUTHOR_EMAIL = "author_email";
    private static final String KEY_PLATFORM = "platform";
    private static final String KEY_DISTRIBUTION = "distribution";
    private static final String KEY_DISTRIBUTION_VERSION = "distribution_version";

    public Sdk(String json) throws JSONException {
        super(json);
    }

    public Sdk() {
        super();
    }

    public void initBaseType() {
        setBaseType(BaseType.sdk);
    }

    public String getVersion() {
        try {
            if (!isNull(KEY_VERSION)) {
                return getString(KEY_VERSION);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setVersion(String version) {
        try {
            put(KEY_VERSION, version);
        } catch (JSONException e) {
            Log.w("Error adding %s to Sdk.", KEY_VERSION);
        }
    }

    public String getProgrammingLanguage() {
        try {
            if (!isNull(KEY_PROGRAMMING_LANGUAGE)) {
                return getString(KEY_PROGRAMMING_LANGUAGE);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        try {
            put(KEY_PROGRAMMING_LANGUAGE, programmingLanguage);
        } catch (JSONException e) {
            Log.w("Error adding %s to Sdk.", KEY_PROGRAMMING_LANGUAGE);
        }
    }

    public String getAuthorName() {
        try {
            if (!isNull(KEY_AUTHOR_NAME)) {
                return getString(KEY_AUTHOR_NAME);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setAuthorName(String authorName) {
        try {
            put(KEY_AUTHOR_NAME, authorName);
        } catch (JSONException e) {
            Log.w("Error adding %s to Sdk.", KEY_AUTHOR_NAME);
        }
    }

    public String getAuthorEmail() {
        try {
            if (!isNull(KEY_AUTHOR_EMAIL)) {
                return getString(KEY_AUTHOR_EMAIL);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setAuthorEmail(String authorEmail) {
        try {
            put(KEY_AUTHOR_EMAIL, authorEmail);
        } catch (JSONException e) {
            Log.w("Error adding %s to Sdk.", KEY_AUTHOR_EMAIL);
        }
    }

    public String getPlatform() {
        try {
            if (!isNull(KEY_PLATFORM)) {
                return getString(KEY_PLATFORM);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setPlatform(String platform) {
        try {
            put(KEY_PLATFORM, platform);
        } catch (JSONException e) {
            Log.w("Error adding %s to Sdk.", KEY_PLATFORM);
        }
    }

    public String getDistribution() {
        try {
            if (!isNull(KEY_DISTRIBUTION)) {
                return getString(KEY_DISTRIBUTION);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setDistribution(String distribution) {
        try {
            put(KEY_DISTRIBUTION, distribution);
        } catch (JSONException e) {
            Log.w("Error adding %s to Sdk.", KEY_DISTRIBUTION);
        }
    }

    public String getDistributionVersion() {
        try {
            if (!isNull(KEY_DISTRIBUTION_VERSION)) {
                return getString(KEY_DISTRIBUTION_VERSION);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setDistributionVersion(String distributionVersion) {
        try {
            put(KEY_DISTRIBUTION_VERSION, distributionVersion);
        } catch (JSONException e) {
            Log.w("Error adding %s to Sdk.", KEY_DISTRIBUTION_VERSION);
        }
    }


}
