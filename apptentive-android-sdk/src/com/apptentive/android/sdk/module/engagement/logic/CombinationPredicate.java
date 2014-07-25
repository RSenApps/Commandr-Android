/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.logic;

import android.content.Context;

import com.apptentive.android.sdk.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Sky Kelsey
 */
public class CombinationPredicate extends Predicate {

    protected String operationKey;
    protected Operation operation;
    protected List<Predicate> children;

    protected CombinationPredicate(String operationKey, Object object) throws JSONException {
        this.operationKey = operationKey;
        operation = Operation.parse(operationKey);
        this.children = new ArrayList<Predicate>();
        if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject child = (JSONObject) jsonArray.get(i);
                children.add(Predicate.parse(null, child));
            }
        } else if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            @SuppressWarnings("unchecked")
            Iterator<String> it = (Iterator<String>) jsonObject.keys();
            while (it.hasNext()) {
                String key = it.next();
                children.add(Predicate.parse(key, jsonObject.get(key)));
            }
        } else {
            Log.w("Unrecognized Combination Predicate: %s", object.toString());
        }

    }

    public boolean apply(Context context) {
        try {
            Log.v("Start: Combination Predicate: %s", operation.name());
            if (operation == Operation.$and) {
                for (Predicate predicate : children) {
                    boolean ret = predicate.apply(context);
                    Log.v("=> %b", ret);
                    if (!ret) {
                        return false;
                    }
                }
                return true;
            } else if (operation == Operation.$or) {
                for (Predicate predicate : children) {
                    boolean ret = predicate.apply(context);
                    Log.v("=> %b", ret);
                    if (ret) {
                        return true;
                    }
                }
                return false;
            } else if (operation == Operation.$not) {
                if (children.size() != 1) {
                    throw new IllegalArgumentException("$not condition must have exactly one child, has ." + children.size());
                }
                Predicate predicate = children.get(0);
                boolean ret = !predicate.apply(context);
                Log.v("=> %b", ret);
                return ret;
            } else {
                // Unsupported
                Log.v("Unsupported operation: \"%s\" => false", operationKey);
                return false;
            }
        } finally {
            Log.v("End:   Combination Predicate: %s", operation.name());
        }
    }
}
