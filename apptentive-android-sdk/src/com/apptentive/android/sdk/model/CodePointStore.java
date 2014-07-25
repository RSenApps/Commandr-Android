/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.util.Constants;
import com.apptentive.android.sdk.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * All public methods altering code point values should be synchronized.<p/>
 * Example:
 * <pre>
 * {
 *   "code_point": {
 *     "codePoint1": {
 *       "last": 1234567890,
 *       "total": 6,
 *       "version": {
 *         "1.1": 4,
 *         "1.2": 2
 *       },
 *       "build": {
 *         "5": 4,
 *         "6": 2
 *       }
 *     }
 *   },
 *   "interactions": {
 *     "526fe2836dd8bf546a00000c": {
 *       "last": 1234567890.4,
 *       "total": 6,
 *       "version": {
 *         "1.1": 4,
 *         "1.2": 2
 *       },
 *       "build": {
 *         "5": 4,
 *         "6": 2
 *       }
 *     }
 *   }
 * }
 * </pre>
 *
 * @author Sky Kelsey
 */
public class CodePointStore extends JSONObject {

    public static final String KEY_CODE_POINT = "code_point";
    public static final String KEY_INTERACTIONS = "interactions";
    public static final String KEY_LAST = "last"; // The last time this codepoint was seen.
    public static final String KEY_TOTAL = "total"; // The total times this code point was seen.
    public static final String KEY_VERSION = "version";
    public static final String KEY_BUILD = "build";

    private static CodePointStore instance;

    private CodePointStore() {
        super();
    }

    private CodePointStore(String json) throws JSONException {
        super(json);
    }

    private static CodePointStore getInstance(Context context) {
        if (instance == null) {
            instance = load(context);
        }
        return instance;
    }

    private static void save(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.PREF_KEY_CODE_POINT_STORE, instance.toString()).commit();
    }

    private static CodePointStore load(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return CodePointStore.load(prefs);
    }

    private static CodePointStore load(SharedPreferences prefs) {
        String json = prefs.getString(Constants.PREF_KEY_CODE_POINT_STORE, null);
        try {
            if (json != null) {
                return new CodePointStore(json);
            }
        } catch (JSONException e) {
            Log.e("Error loading CodePointStore from SharedPreferences.", e);
        }
        return new CodePointStore();
    }

    public static synchronized void storeCodePointForCurrentAppVersion(Context context, String fullCodePoint) {
        storeRecordForCurrentAppVersion(context, false, fullCodePoint);
    }

    public static synchronized void storeInteractionForCurrentAppVersion(Context context, String fullCodePoint) {
        storeRecordForCurrentAppVersion(context, true, fullCodePoint);
    }

    private static void storeRecordForCurrentAppVersion(Context context, boolean isInteraction, String fullCodePoint) {
        String version = Util.getAppVersionName(context);
        int build = Util.getAppVersionCode(context);
        storeRecord(context, isInteraction, fullCodePoint, version, build);
    }

    public static synchronized void storeRecord(Context context, boolean isInteraction, String fullCodePoint, String version, int build) {
        storeRecord(context, isInteraction, fullCodePoint, version, build, Util.currentTimeSeconds());
    }

    public static synchronized void storeRecord(Context context, boolean isInteraction, String fullCodePoint, String version, int build, double currentTimeSeconds) {
        String buildString = String.valueOf(build);
        CodePointStore store = getInstance(context);
        if (store != null && fullCodePoint != null && version != null) {
            try {
                String recordTypeKey = isInteraction ? KEY_INTERACTIONS : KEY_CODE_POINT;
                JSONObject recordType;
                if (!store.isNull(recordTypeKey)) {
                    recordType = store.getJSONObject(recordTypeKey);
                } else {
                    recordType = new JSONObject();
                    store.put(recordTypeKey, recordType);
                }

                // Get or create code point object.
                JSONObject codePointJson;
                if (!recordType.isNull(fullCodePoint)) {
                    codePointJson = recordType.getJSONObject(fullCodePoint);
                } else {
                    codePointJson = new JSONObject();
                    recordType.put(fullCodePoint, codePointJson);
                }

                // Set the last time this code point was seen.
                codePointJson.put(KEY_LAST, currentTimeSeconds);

                // Increment the total times this code point was seen.
                int total = 0;
                if (codePointJson.has(KEY_TOTAL)) {
                    total = codePointJson.getInt(KEY_TOTAL);
                }
                codePointJson.put(KEY_TOTAL, total + 1);

                // Get or create version object.
                JSONObject versionJson;
                if (!codePointJson.isNull(KEY_VERSION)) {
                    versionJson = codePointJson.getJSONObject(KEY_VERSION);
                } else {
                    versionJson = new JSONObject();
                    codePointJson.put(KEY_VERSION, versionJson);
                }

                // Set count for current version.
                int existingVersionCount = 0;
                if (!versionJson.isNull(version)) {
                    existingVersionCount = versionJson.getInt(version);
                }
                versionJson.put(version, existingVersionCount + 1);

                // Get or create build object.
                JSONObject buildJson;
                if (!codePointJson.isNull(KEY_BUILD)) {
                    buildJson = codePointJson.getJSONObject(KEY_BUILD);
                } else {
                    buildJson = new JSONObject();
                    codePointJson.put(KEY_BUILD, buildJson);
                }

                // Set count for the current build
                int existingBuildCount = 0;
                if (!buildJson.isNull(buildString)) {
                    existingBuildCount = buildJson.getInt(buildString);
                }
                buildJson.put(buildString, existingBuildCount + 1);

                save(context);
            } catch (JSONException e) {
                Log.w("Unable to store code point %s.", e, fullCodePoint);
            }
        }
    }

    public static JSONObject getRecord(Context context, boolean interaction, String name) {
        CodePointStore store = getInstance(context);
        String recordTypeKey = interaction ? KEY_INTERACTIONS : KEY_CODE_POINT;
        try {
            if (!store.isNull(recordTypeKey)) {
                if (store.has(recordTypeKey)) {
                    JSONObject recordType = store.getJSONObject(recordTypeKey);
                    if (recordType.has(name)) {
                        return recordType.getJSONObject(name);
                    }
                }
            }
        } catch (JSONException e) {
            Log.w("Error loading code point record for \"%s\"", name);
        }
        return null;
    }

    public static Long getTotalInvokes(Context context, boolean interaction, String name) {
        try {
            JSONObject record = getRecord(context, interaction, name);
            if (record != null && record.has(KEY_TOTAL)) {
                return record.getLong(KEY_TOTAL);
            }
        } catch (JSONException e) {
        }
        return 0l;
    }

    public static Double getLastInvoke(Context context, boolean interaction, String name) {
        try {
            JSONObject record = getRecord(context, interaction, name);
            if (record != null && record.has(KEY_LAST)) {
                return record.getDouble(KEY_LAST);
            }
        } catch (JSONException e) {
        }
        return 0d;
    }

    public static Long getVersionInvokes(Context context, boolean interaction, String name, String version) {
        try {
            JSONObject record = getRecord(context, interaction, name);
            if (record != null && record.has(KEY_VERSION)) {
                JSONObject versionJson = record.getJSONObject(KEY_VERSION);
                if (versionJson.has(version)) {
                    return versionJson.getLong(version);
                }
            }
        } catch (JSONException e) {
        }
        return 0l;
    }

    public static Long getBuildInvokes(Context context, boolean interaction, String name, String build) {
        try {
            JSONObject record = getRecord(context, interaction, name);
            if (record != null && record.has(KEY_BUILD)) {
                JSONObject buildJson = record.getJSONObject(KEY_BUILD);
                if (buildJson.has(build)) {
                    return buildJson.getLong(build);
                }
            }
        } catch (JSONException e) {
        }
        return 0l;
    }

    public static String toString(Context context) {
        return "CodePointStore:  " + getInstance(context).toString();
    }

    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(Constants.PREF_KEY_CODE_POINT_STORE).commit();
        instance = null;
    }
}
