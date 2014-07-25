package com.apptentive.android.sdk.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.util.Constants;
import com.apptentive.android.sdk.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sky Kelsey
 */
public class VersionHistoryStore {
    public static final String FIELD_SEP = "--";
    public static final String ENTRY_SEP = "__";


    /**
     * Two simple lists of version number and update time. Version number is base64 encoded and followed by a ':" and the
     * timestamp. Each entry ends with a ';' to separate it from subsequent entries. A list is stored for both the
     * version name and version code.
     */
    public static void updateVersionHistory(Context context, Integer newVersionCode, String newVersionName) {
        updateVersionHistory(context, newVersionCode, newVersionName, Util.currentTimeSeconds());
    }

    public static void updateVersionHistory(Context context, Integer newVersionCode, String newVersionName, double date) {
        Log.d("Updating version info: %d, %s @%f", newVersionCode, newVersionName, date);
        List<VersionHistoryEntry> versionHistory = getVersionHistory(context);
        boolean alreadySawVersionCode = false;
        boolean alreadySawVersionName = false;
        for (VersionHistoryEntry entry : versionHistory) {
            if (newVersionCode.equals(entry.versionCode)) {
                alreadySawVersionCode = true;
            }
            if (newVersionName.equals(entry.versionName)) {
                alreadySawVersionName = true;
            }
        }
        // If either the versionCode or versionName are new, record a new update.
        if (!alreadySawVersionCode || !alreadySawVersionName) {
            versionHistory.add(new VersionHistoryEntry(date, newVersionCode, newVersionName));
            saveVersionHistory(context, versionHistory);
        }
    }


    /**
     * Returns the number of seconds since the first time we saw this release of the app. Since the version entries are
     * always stored in order, the first matching entry happened first.
     *
     * @param selector - The type of version entry we are looking for: total, version, or build.
     * @return A Double representing the number of seconds since we first saw the desired app release entry. Null if never seen.
     */
    public static Double getTimeSinceVersionFirstSeen(Context context, Selector selector) {
        List<VersionHistoryEntry> entries = getVersionHistory(context);
        VersionHistoryEntry matchingEntry = null;
        if (entries != null) {
            outer_loop:
            for (VersionHistoryEntry entry : entries) {
                switch (selector) {
                    case total:
                        // Grab the first entry.
                        matchingEntry = entry;
                        break outer_loop;
                    case version:
                        if (entry.versionName.equals(Util.getAppVersionName(context))) {
                            matchingEntry = entry;
                            break outer_loop;
                        }
                        continue;
                    case build:
                        if (entry.versionCode.equals(Util.getAppVersionCode(context))) {
                            matchingEntry = entry;
                            break outer_loop;
                        }
                        continue;
                    default:
                        return null;
                }
            }
        }
        if (matchingEntry != null) {
            return Util.currentTimeSeconds() - matchingEntry.seconds;
        }
        return null;
    }

    /**
     * Returns true if the current version or build is not the first version or build that we have seen. Basically, it just
     * looks for two or more versions or builds.
     *
     * @param selector - The type of version entry we are looking for: version, or build.
     * @return True if there are records with more than one version or build, depending on the value of selector.
     */
    public static boolean isUpdate(Context context, Selector selector) {
        List<VersionHistoryEntry> entries = getVersionHistory(context);
        Set uniques = new HashSet();
        if (entries != null) {
            for (VersionHistoryEntry entry : entries) {
                switch (selector) {
                    case version:
                        uniques.add(entry.versionName);
                        break;
                    case build:
                        uniques.add(entry.versionCode);
                        break;
                    default:
                        break;
                }
            }
        }
        return uniques.size() > 1;
    }

    public static VersionHistoryEntry getLastVersionSeen(Context context) {
        List<VersionHistoryEntry> entries = getVersionHistory(context);
        if (entries != null && !entries.isEmpty()) {
            return entries.get(entries.size() - 1);
        }
        return null;
    }

    /**
     * Returns a map containing two maps of string to string. The first map is named "versionName" and the second is named
     * "versionCode". Each of those maps from a Double timestamp to a String representing the version code of version name.
     *
     * @return A List of VersionHistoryEntry objects.
     */
    public static List<VersionHistoryEntry> getVersionHistory(Context context) {
        List<VersionHistoryEntry> versionHistory = new ArrayList<VersionHistoryEntry>();
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String versionHistoryString = prefs.getString(Constants.PREF_KEY_VERSION_HISTORY, null);
        if (versionHistoryString != null) {
            String[] parts = versionHistoryString.split(ENTRY_SEP);
            for (int i = 0; i < parts.length; i++) {
                versionHistory.add(new VersionHistoryEntry(parts[i]));
            }
        }
        return versionHistory;
    }

    private static void saveVersionHistory(Context context, List<VersionHistoryEntry> versionHistory) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        StringBuilder versionHistoryString = new StringBuilder();
        for (VersionHistoryEntry entry : versionHistory) {
            versionHistoryString.append(entry.toString()).append(ENTRY_SEP);
        }
        prefs.edit().putString(Constants.PREF_KEY_VERSION_HISTORY, versionHistoryString.toString()).commit();
    }

    public enum Selector {
        total,
        version,
        build,
        other;

        public static Selector parse(String name) {
            try {
                return Selector.valueOf(name);
            } catch (IllegalArgumentException e) {
            }
            return other;
        }
    }

    public static class VersionHistoryEntry {
        public Double seconds;
        public Integer versionCode;
        public String versionName;

        public VersionHistoryEntry(String encoded) {
            if (encoded != null) {
                // Remove entry separator and split on field separator.
                String[] parts = encoded.replace(ENTRY_SEP, "").split(FIELD_SEP);
                seconds = Double.valueOf(parts[0]);
                versionCode = Integer.parseInt(parts[1]);
                versionName = parts[2];
            }
        }

        public VersionHistoryEntry(Double seconds, Integer versionCode, String versionName) {
            this.seconds = seconds;
            this.versionCode = versionCode;
            this.versionName = versionName;
        }

        @Override
        public String toString() {
            return String.valueOf(seconds) + FIELD_SEP + String.valueOf(versionCode) + FIELD_SEP + versionName;
        }
    }
}
