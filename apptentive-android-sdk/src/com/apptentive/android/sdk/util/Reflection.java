/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.util;

import android.os.Build;

import com.apptentive.android.sdk.Log;

import java.lang.reflect.Method;

/**
 * This class is used to allow access of fields, methods, and classes that are not available in the targeted API level.
 *
 * @author Sky Kelsey.
 */
public class Reflection {

    /**
     * <p>Returns the Build.BOOTLOADER version String. This field is introduced in API level 8</p>
     * <p>This field appears to be implemented sporadically.
     * <ul>
     * <li>Galaxy S Captivate on 2.1: BOOTLOADER = null (Expected, as this has only API 7)</li>
     * <li>Nexus S on 2.3.6: BOOTLOADER = I9020XXKA3</li>
     * <li>Atrix on 2.2.2: BOOTLOADER = unknown</li>
     * <li>Galaxy Tab 10.1 on 3.1: BOOTLOADER = P7500XXKG8</li>
     * </ul>
     * </p>
     *
     * @return String The Build.BOOTLOADER version String.
     */
    public static String getBootloaderVersion() {
        try {
            return (String) Build.class.getField("BOOTLOADER").get(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <p>Returns the radio version string. Build.getRadioVersion() is attempted first. It requires reflection, since it
     * was introduced in API 14. Failing that, Build.RADIO is accessed. This is not preferred, as this variable may not
     * have been initialized before the Build object is initialized. It requires reflection as well, as it was introduced
     * in API 8</p>
     * <p>This field appears to be implemented sporadically.
     * <ul>
     * <li>Galaxy S Captivate on 2.1: RADIO = null (Expected, as this has only API 7)</li>
     * <li>Nexus S on 2.3.6: RADIO = unknown</li>
     * <li>Atrix on 2.2.2: RADIO = unknown</li>
     * <li>Galaxy Tab 10.1 on 3.1: RADIO = unknown</li>
     * <li>Galaxy Nexus on 4.2.1: RADIO = I9250XXLJ1
     * </ul>
     * </p>
     *
     * @return Either Build.getRadioVersion(), or Build.BUILD, or null.
     */
    public static String getRadioVersion() {
        try {
            Method method = Build.class.getMethod("getRadioVersion");
            Object invoked = method.invoke(null);
            String ret = invoked.toString();
            Log.v("Build.getRadioVersion() = %s", ret);
            return ret;
        } catch (Exception e) {
            try {
                Log.v("Build.RADIO = %s", (String) Build.class.getField("RADIO").get(null));
                return (String) Build.class.getField("RADIO").get(null);
            } catch (Exception f) {
                return null;
            }
        }
    }
}
