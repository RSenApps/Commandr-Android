/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.logic;

import android.content.Context;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.model.AppRelease;
import com.apptentive.android.sdk.model.CodePointStore;
import com.apptentive.android.sdk.model.CustomData;
import com.apptentive.android.sdk.model.Device;
import com.apptentive.android.sdk.model.Person;
import com.apptentive.android.sdk.model.Sdk;
import com.apptentive.android.sdk.storage.AppReleaseManager;
import com.apptentive.android.sdk.storage.DeviceManager;
import com.apptentive.android.sdk.storage.PersonManager;
import com.apptentive.android.sdk.storage.SdkManager;
import com.apptentive.android.sdk.storage.VersionHistoryStore;
import com.apptentive.android.sdk.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Sky Kelsey
 */
public class ComparisonPredicate extends Predicate {

    protected String query;
    protected List<Condition> conditions;

    public ComparisonPredicate(String query, Object condition) throws JSONException {
        this.query = query;
        conditions = new ArrayList<Condition>();
        if (condition instanceof JSONObject) {
            JSONObject conditionJson = (JSONObject) condition;
            // This is an object. It may contain multiple comparison operations, so unroll it and add them all in.
            @SuppressWarnings("unchecked")
            Iterator<String> it = (Iterator<String>) conditionJson.keys();
            while (it.hasNext()) {
                String conditionKey = it.next();
                Operation operation = Operation.valueOf(conditionKey);
                conditions.add(new Condition(operation, conditionJson.get(conditionKey)));
            }
        } else {
            // If it's a literal, then it has to be an EQUALITY operation. The others are always wrapped in a JSONObject.
            conditions.add(new Condition(Operation.$eq, condition));
        }
    }

    /**
     * Makes sure that if the first parameter is a Double, the second is converted to a Double. If the first parameter is
     * not a Double, or the second can't be converted to a Double, then simply return the second parameter.
     *
     * @return The second parameter, converted to a Double if it is a Number, and the first parameter is a Double. Else
     * return the second parameter straight away.
     */
    private Object normalize(Object one, Object two) {
        if (one instanceof Double && two instanceof Number) {
            return ((Number) two).doubleValue();
        }
        return two;
    }

    public Comparable getValue(Context context, String query) {

        String[] tokens = query.split("/");
        QueryType queryType = QueryType.parse(tokens[0]);

        switch (queryType) {
            case application_version:
                return Util.getAppVersionName(context);
            case application_build:
                return (double) Util.getAppVersionCode(context);
            case current_time:
                return Util.currentTimeSeconds();
            case is_update: {
                ValueSubFilterType subFilterType = ValueSubFilterType.parse(tokens[1]);
                Boolean isUpdate = false;
                switch (subFilterType) {
                    case version:
                        isUpdate = VersionHistoryStore.isUpdate(context, VersionHistoryStore.Selector.version);
                        break;
                    case build:
                        isUpdate = VersionHistoryStore.isUpdate(context, VersionHistoryStore.Selector.build);
                        break;
                    default:
                        Log.w("Unsupported sub filter type \"%s\" for query \"%s\"", subFilterType.name(), query);
                }
                return isUpdate;
            }
            case time_since_install: {
                ValueSubFilterType subFilterType = ValueSubFilterType.parse(tokens[1]);
                Double seconds = null;
                switch (subFilterType) {
                    case total:
                        seconds = VersionHistoryStore.getTimeSinceVersionFirstSeen(context, VersionHistoryStore.Selector.total);
                        break;
                    case version:
                        seconds = VersionHistoryStore.getTimeSinceVersionFirstSeen(context, VersionHistoryStore.Selector.version);
                        break;
                    case build:
                        seconds = VersionHistoryStore.getTimeSinceVersionFirstSeen(context, VersionHistoryStore.Selector.build);
                        break;
                    default:
                        Log.w("Unsupported sub filter type \"%s\" for query \"%s\"", subFilterType.name(), query);
                }
                return seconds;
            }
            case interactions:
                // Handled same as interactions below.
            case code_point:
                boolean isInteraction = queryType.equals(QueryType.interactions);
                String name = tokens[1];
                ValueFilterType valueFilterType = ValueFilterType.parse(tokens[2]);
                ValueSubFilterType valueSubFilterType = ValueSubFilterType.parse(tokens[3]);

                switch (valueFilterType) {
                    case invokes:
                        switch (valueSubFilterType) {
                            case total: // Get total for all versions of the app.
                                return (double) CodePointStore.getTotalInvokes(context, isInteraction, name);
                            case version:
                                String appVersion = Util.getAppVersionName(context);
                                return (double) CodePointStore.getVersionInvokes(context, isInteraction, name, appVersion);
                            case build:
                                String appBuild = String.valueOf(Util.getAppVersionCode(context));
                                return (double) CodePointStore.getBuildInvokes(context, isInteraction, name, appBuild);
                            case time_ago:
                                double timeAgo = Util.currentTimeSeconds() - CodePointStore.getLastInvoke(context, isInteraction, name);
                                return timeAgo;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            case person: {
                String key = tokens[1];
                boolean queryCustomData = false;
                if (key.equals("custom_data")) {
                    queryCustomData = true;
                    key = tokens[2];
                }
                Person person = PersonManager.getStoredPerson(context);
                if (queryCustomData) {
                    CustomData customData = person.getCustomData();
                    if (customData != null) {
                        return (Comparable) customData.opt(key);
                    }
                } else {
                    return (Comparable) person.opt(key);
                }
            }
            case device: {
                String key = tokens[1];
                boolean queryCustomData = false;
                if (key.equals(Device.KEY_CUSTOM_DATA)) {
                    queryCustomData = true;
                    key = tokens[2];
                }
                Device device = DeviceManager.getStoredDevice(context);
                if (queryCustomData) {
                    CustomData customData = device.getCustomData();
                    if (customData != null) {
                        return (Comparable) customData.opt(key);
                    }
                } else {
                    return (Comparable) device.opt(key);
                }
            }
            case app_release: {
                String key = tokens[1];
                AppRelease appRelease = AppReleaseManager.getStoredAppRelease(context);
                return (Comparable) appRelease.opt(key);
            }
            case sdk:
                String key = tokens[1];
                Sdk sdk = SdkManager.getStoredSdk(context);
                return (Comparable) sdk.opt(key);
            default:
                break;
        }
        return null;
    }

    @Override
    public boolean apply(Context context) {
        Log.v("Comparison Predicate: %s", query);
        Comparable value = getValue(context, query);
        Log.v("   => %s", value);
        for (Condition condition : conditions) {
            condition.operand = normalize(value, condition.operand);
            Log.v("-- Compare: %s %s %s", getLoggableValue(value), condition.operation, getLoggableValue(condition.operand));
            switch (condition.operation) {
                case $gt: {
                    if (value == null) {
                        return false;
                    }
                    if (condition.operand instanceof Comparable) {
                        Comparable operand = (Comparable) condition.operand;
                        if (!(value.compareTo(operand) > 0)) {
                            return false;
                        }
                    } else {
                        throw new IllegalArgumentException(String.format("Can't compare %s > %s", value, condition.operand));
                    }
                    break;
                }
                case $gte: {
                    if (value == null) {
                        return false;
                    }
                    if (condition.operand instanceof Comparable) {
                        Comparable operand = (Comparable) condition.operand;
                        if (!(value.compareTo(operand) >= 0)) {
                            return false;
                        }
                    } else {
                        throw new IllegalArgumentException(String.format("Can't compare %s >= %s", value, condition.operand));
                    }
                    break;
                }
                case $eq: {
                    if (value == null) {
                        return false;
                    }
                    if (condition.operand instanceof Comparable) {
                        Comparable operand = (Comparable) condition.operand;
                        if (!value.equals(operand)) {
                            return false;
                        }
                    } else {
                        throw new IllegalArgumentException(String.format("Can't compare %s == %s", value, condition.operand));
                    }
                    break;
                }
                case $ne: {
                    if (value == null) {
                        return false;
                    }
                    if (condition.operand instanceof Comparable) {
                        Comparable operand = (Comparable) condition.operand;
                        if (value.equals(operand)) {
                            return false;
                        }
                    } else {
                        throw new IllegalArgumentException(String.format("Can't compare %s != %s", value, condition.operand));
                    }
                    break;
                }
                case $lte: {
                    if (value == null) {
                        return false;
                    }
                    if (condition.operand instanceof Comparable) {
                        Comparable operand = (Comparable) condition.operand;
                        if (!(value.compareTo(operand) <= 0)) {
                            return false;
                        }
                    } else {
                        throw new IllegalArgumentException(String.format("Can't compare %s <= %s", value, condition.operand));
                    }
                    break;
                }
                case $lt: {
                    if (value == null) {
                        return false;
                    }
                    if (condition.operand instanceof Comparable) {
                        Comparable operand = (Comparable) condition.operand;
                        if (!(value.compareTo(operand) < 0)) {
                            return false;
                        }
                    } else {
                        throw new IllegalArgumentException(String.format("Can't compare %s < %s", value, condition.operand));
                    }
                    break;
                }
                case $exists: {
                    if (!(condition.operand instanceof Boolean)) {
                        throw new IllegalArgumentException(String.format("Argument %s is not a boolean", condition.operand));
                    }
                    boolean shouldExist = (Boolean) condition.operand;
                    boolean exists = value != null;
                    return exists == shouldExist;
                }
                case $contains: {
                    if (value == null) {
                        return false;
                    }
                    boolean ret = false;
                    if (value instanceof String && condition.operand instanceof String) {
                        ret = ((String) value).toLowerCase().contains(((String) condition.operand).toLowerCase());
                    }
                    return ret;
                }
                case $starts_with: {
                    if (value == null) {
                        return false;
                    }
                    boolean ret = false;
                    if (value instanceof String && condition.operand instanceof String) {
                        ret = ((String) value).toLowerCase().startsWith(((String) condition.operand).toLowerCase());
                    }
                    return ret;
                }
                case $ends_with: {
                    if (value == null) {
                        return false;
                    }
                    boolean ret = false;
                    if (value instanceof String && condition.operand instanceof String) {
                        ret = ((String) value).toLowerCase().endsWith(((String) condition.operand).toLowerCase());
                    }
                    return ret;
                }
                default:
                    break;
            }
        }
        return true;
    }

    private String getLoggableValue(Object input) {
        if (input != null) {
            if (input instanceof String) {
                return "\"" + input + "\"";
            } else {
                return input.toString();
            }
        } else {
            return "null";
        }
    }

    private enum QueryType {
        application_version,
        application_build,
        current_time,
        is_update,
        time_since_install,
        code_point,
        interactions,
        person,
        device,
        app_release,
        sdk,
        other;

        public static QueryType parse(String name) {
            try {
                return QueryType.valueOf(name);
            } catch (IllegalArgumentException e) {
            }
            return other;
        }
    }

    private enum ValueFilterType {
        invokes,
        other;

        public static ValueFilterType parse(String name) {
            try {
                return ValueFilterType.valueOf(name);
            } catch (IllegalArgumentException e) {
            }
            return other;
        }
    }

    private enum ValueSubFilterType {
        total,
        version,
        build,
        time_ago,
        other;

        public static ValueSubFilterType parse(String name) {
            try {
                return ValueSubFilterType.valueOf(name);
            } catch (IllegalArgumentException e) {
            }
            return other;
        }
    }
}
