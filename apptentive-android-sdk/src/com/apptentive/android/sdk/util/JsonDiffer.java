/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.util;

import com.apptentive.android.sdk.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Sky Kelsey
 */
public class JsonDiffer {

    public static JSONObject getDiff(JSONObject original, JSONObject updated) {
        JSONObject ret = new JSONObject();

        Set<String> originalKeys = getKeys(original);
        Set<String> updatedKeys = getKeys(updated);

        Iterator<String> it = originalKeys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            updatedKeys.remove(key);
            try {
                Object oldValue = original.opt(key);
                Object newValue = updated.opt(key);

                if (isEmpty(oldValue)) {
                    if (!isEmpty(newValue)) {
                        // Old is empty. New is not. Update.
                        ret.put(key, newValue);
                    }
                } else if (isEmpty(newValue)) {
                    // Old is not empty, but new is empty. Clear value.
                    ret.put(key, JSONObject.NULL);
                } else if (oldValue instanceof JSONObject && newValue instanceof JSONObject) {
                    // Diff JSONObjects
                    if (!areObjectsEqual(oldValue, newValue)) {
                        ret.put(key, newValue);
                    }
                } else if (oldValue instanceof JSONArray && newValue instanceof JSONArray) {
                    // Diff JSONArrays
                    // TODO: At least check for strict equality. Right now, we always send nested JSONArrays.
                    ret.put(key, newValue);
                } else if (!oldValue.equals(newValue)) {
                    // Diff primitives
                    ret.put(key, newValue);
                } else if (oldValue.equals(newValue)) {
                    // Do nothing.
                }
            } catch (JSONException e) {
                Log.w("Error diffing object with key %s", e, key);
            } finally {
                it.remove();
            }
        }

        // Finally, add in the keys that were added in the new object.
        it = updatedKeys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            try {
                ret.put(key, updated.get(key));
            } catch (JSONException e) {
                // This can't happen.
                continue;
            }
        }

        // If there is no difference, return null.
        if (ret.length() == 0) {
            ret = null;
        }
        Log.v("Generated diff: %s", ret);
        return ret;
    }


    public static boolean areObjectsEqual(Object left, Object right) {
        if (left == right) return true;
        if (left == null || right == null) return false;

        if (left instanceof JSONObject && right instanceof JSONObject) {
            JSONObject leftJSONObject = (JSONObject) left;
            JSONObject rightJSONObject = (JSONObject) right;
            if (leftJSONObject.length() != rightJSONObject.length()) {
                return false;
            }
            Iterator keys = leftJSONObject.keys();
            while (keys.hasNext()) {
                try {
                    String key = (String) keys.next();
                    Object leftValue = leftJSONObject.get(key);
                    Object rightValue = rightJSONObject.get(key);
                    if (!areObjectsEqual(leftValue, rightValue)) {
                        return false;
                    }
                } catch (JSONException e) {
                    Log.w("Error comparing JSONObjects", e);
                    return false;
                }
            }
            return true;
        } else if (left instanceof JSONArray && right instanceof JSONArray) {
            // TODO: Figure out how to do this efficiently. Maybe since ordering is important, this is actually rather simple.
            return false;
        } else {
            return left.equals(right);
        }
    }

    private static Set<String> getKeys(JSONObject jsonObject) {
        Set<String> keys = new HashSet<String>();
        if (jsonObject != null) {
            @SuppressWarnings("unchecked")
            Iterator<String> it = (Iterator<String>) jsonObject.keys();
            while (it.hasNext()) {
                keys.add(it.next());
            }
        }
        return keys;
    }

    private static boolean isEmpty(Object value) {
        return value == null || value.equals("");
    }
}
