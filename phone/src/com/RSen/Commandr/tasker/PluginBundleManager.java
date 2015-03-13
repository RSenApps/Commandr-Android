/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.RSen.Commandr.tasker;

import android.content.Context;
import android.os.Bundle;


public final class PluginBundleManager {
    /**
     * Type: {@code boolean}.
     * <p/>
     * True means display is on. False means off.
     */
    public static final String BUNDLE_EXTRA_STRING_PHRASE =
            "com.RSen.Commandr.extra.STRING_PHRASE"; //$NON-NLS-1$

    public static final String BUNDLE_EXTRA_REGEX =
            "com.RSen.Commandr.extra.REGEX"; //$NON-NLS-1$

    /**
     * Method to verify the content of the bundle are correct.
     * <p/>
     * This method will not mutate {@code bundle}.
     *
     * @param bundle bundle to verify. May be null, which will always return false.
     * @return true if the Bundle is valid, false if the bundle is invalid.
     */
    public static boolean isBundleValid(final Bundle bundle) {
        if (null == bundle) {
            return false;
        }

        /*
         * Make sure the expected extras exist
         */
        if (!bundle.containsKey(BUNDLE_EXTRA_STRING_PHRASE)) {

            return false;
        }


        /*
         * Make sure the extra is the correct type
         */
        if (bundle.getString(BUNDLE_EXTRA_STRING_PHRASE, "") != bundle.getString(BUNDLE_EXTRA_STRING_PHRASE,
                "x")) {

            return false;
        }

        return true;
    }

    /**
     * @param context Application context.
     * @return A plug-in bundle.
     */
    public static Bundle generateBundle(final Context context, final String phrase,final boolean is_regex) {
        final Bundle result = new Bundle();
        result.putString(BUNDLE_EXTRA_STRING_PHRASE, phrase);
        result.putBoolean(BUNDLE_EXTRA_REGEX,is_regex);
        return result;
    }

    /**
     * Private constructor prevents instantiation
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private PluginBundleManager() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}