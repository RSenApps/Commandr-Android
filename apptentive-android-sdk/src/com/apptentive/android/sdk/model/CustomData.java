package com.apptentive.android.sdk.model;

import com.apptentive.android.sdk.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Sky Kelsey
 */
public class CustomData extends JSONObject {


    public CustomData() throws JSONException {
        super();
    }

    public CustomData(String json) throws JSONException {
        super(json);
    }

    public CustomData(JSONObject object) throws JSONException {
        super(object.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CustomData) {
            if (this == o) {
                return true;
            }

            JSONObject left = this;
            JSONObject right = (JSONObject) o;

            if (left.length() != right.length()) {
                return false;
            }

            Map<String, String> leftMap = new HashMap<String, String>();
            Map<String, String> rightMap = new HashMap<String, String>();

            try {
                Iterator leftIterator = left.keys();
                while (leftIterator.hasNext()) {
                    String key = (String) leftIterator.next();
                    leftMap.put(key, left.getString(key));
                }

                Iterator rightIterator = right.keys();
                while (rightIterator.hasNext()) {
                    String key = (String) rightIterator.next();
                    rightMap.put(key, right.getString(key));
                }
            } catch (JSONException e) {
                Log.e("Error comparing two device data entries: \"%s\"  AND  \"%s\"", left.toString(), right.toString());
            }
            return leftMap.equals(rightMap);
        }
        return false;
    }
}
