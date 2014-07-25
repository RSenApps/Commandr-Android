/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.model.CustomData;
import com.apptentive.android.sdk.model.Person;
import com.apptentive.android.sdk.util.Constants;
import com.apptentive.android.sdk.util.JsonDiffer;

import org.json.JSONException;

/**
 * @author Sky Kelsey
 */
public class PersonManager {

    public static Person storePersonAndReturnDiff(Context context) {
        Person stored = getStoredPerson(context);

        Person current = generateCurrentPerson();
        CustomData customData = loadCustomPersonData(context);
        current.setCustomData(customData);
        String email = loadPersonEmail(context);
        if (email == null) {
            email = loadInitialPersonEmail(context);
        }
        current.setEmail(email);

        Object diff = JsonDiffer.getDiff(stored, current);
        if (diff != null) {
            try {
                storePerson(context, current);
                return new Person(diff.toString());
            } catch (JSONException e) {
                Log.e("Error casting to Person.", e);
            }
        }

        return null;
    }

    /**
     * Provided so we can be sure that the person we send during conversation creation is 100% accurate. Since we do not
     * queue this person up in the payload queue, it could otherwise be lost.
     */
    public static Person storePersonAndReturnIt(Context context) {
        Person current = generateCurrentPerson();

        CustomData customData = loadCustomPersonData(context);
        current.setCustomData(customData);

        String email = loadPersonEmail(context);
        if (email == null) {
            email = loadInitialPersonEmail(context);
        }
        current.setEmail(email);

        storePerson(context, current);
        return current;
    }

    public static CustomData loadCustomPersonData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String personDataString = prefs.getString(Constants.PREF_KEY_PERSON_DATA, null);
        try {
            return new CustomData(personDataString);
        } catch (Exception e) {
        }
        try {
            return new CustomData();
        } catch (JSONException e) {
        }
        return null;
    }

    public static void storeCustomPersonData(Context context, CustomData deviceData) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String personDataString = deviceData.toString();
        prefs.edit().putString(Constants.PREF_KEY_PERSON_DATA, personDataString).commit();
    }

    private static Person generateCurrentPerson() {
        return new Person();
    }

    public static String loadInitialPersonEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PREF_KEY_PERSON_INITIAL_EMAIL, null);
    }

    public static void storeInitialPersonEmail(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.PREF_KEY_PERSON_INITIAL_EMAIL, email).commit();
    }

    public static String loadPersonEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PREF_KEY_PERSON_EMAIL, null);
    }

    public static void storePersonEmail(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.PREF_KEY_PERSON_EMAIL, email).commit();
    }

    public static Person getStoredPerson(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String PersonString = prefs.getString(Constants.PREF_KEY_PERSON, null);
        try {
            return new Person(PersonString);
        } catch (Exception e) {
        }
        return null;
    }

    private static void storePerson(Context context, Person Person) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.PREF_KEY_PERSON, Person.toString()).commit();
    }
}
