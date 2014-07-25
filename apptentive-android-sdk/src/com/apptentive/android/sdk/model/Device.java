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
public class Device extends Payload {

    public static final String KEY = "device";
    public static final String KEY_CUSTOM_DATA = "custom_data";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_OS_NAME = "os_name";
    private static final String KEY_OS_VERSION = "os_version";
    private static final String KEY_OS_BUILD = "os_build";
    private static final String KEY_OS_API_LEVEL = "os_api_level";
    private static final String KEY_MANUFACTURER = "manufacturer";
    private static final String KEY_MODEL = "model";
    private static final String KEY_BOARD = "board";
    private static final String KEY_PRODUCT = "product";
    private static final String KEY_BRAND = "brand";
    private static final String KEY_CPU = "cpu";
    private static final String KEY_DEVICE = "device"; //
    private static final String KEY_CARRIER = "carrier";
    private static final String KEY_CURRENT_CARRIER = "current_carrier";
    private static final String KEY_NETWORK_TYPE = "network_type";
    private static final String KEY_BUILD_TYPE = "build_type";
    private static final String KEY_BUILD_ID = "build_id";
    private static final String KEY_BOOTLOADER_VERSION = "bootloader_version";
    private static final String KEY_RADIO_VERSION = "radio_version";
    private static final String KEY_LOCALE_COUNTRY_CODE = "locale_country_code";
    private static final String KEY_LOCALE_LANGUAGE_CODE = "locale_language_code";
    private static final String KEY_LOCALE_RAW = "locale_raw";
    private static final String KEY_UTC_OFFSET = "utc_offset";
    private static final String KEY_INTEGRATION_CONFIG = "integration_config";


    public Device() {
        super();
    }

    public Device(String json) throws JSONException {
        super(json);
    }

    public void initBaseType() {
        setBaseType(BaseType.device);
    }

    public String getUuid() {
        try {
            if (!isNull(KEY_UUID)) {
                return getString(KEY_UUID);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setUuid(String uuid) {
        try {
            put(KEY_UUID, uuid);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_UUID);
        }
    }

    public String getOsName() {
        try {
            if (!isNull(KEY_OS_NAME)) {
                return getString(KEY_OS_NAME);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setOsName(String osName) {
        try {
            put(KEY_OS_NAME, osName);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_OS_NAME);
        }
    }

    public String getOsVersion() {
        try {
            if (!isNull(KEY_OS_VERSION)) {
                return getString(KEY_OS_VERSION);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setOsVersion(String osVersion) {
        try {
            put(KEY_OS_VERSION, osVersion);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_OS_VERSION);
        }
    }

    public String getOsBuild() {
        try {
            if (!isNull(KEY_OS_BUILD)) {
                return getString(KEY_OS_BUILD);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setOsBuild(String osBuild) {
        try {
            put(KEY_OS_BUILD, osBuild);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_OS_BUILD);
        }
    }

    public String getOsApiLevel() {
        try {
            if (!isNull(KEY_OS_API_LEVEL)) {
                return getString(KEY_OS_API_LEVEL);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setOsApiLevel(String osApiLevel) {
        try {
            put(KEY_OS_API_LEVEL, osApiLevel);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_OS_API_LEVEL);
        }
    }

    public String getManufacturer() {
        try {
            if (!isNull(KEY_MANUFACTURER)) {
                return getString(KEY_MANUFACTURER);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setManufacturer(String manufacturer) {
        try {
            put(KEY_MANUFACTURER, manufacturer);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_MANUFACTURER);
        }
    }

    public String getModel() {
        try {
            if (!isNull(KEY_MODEL)) {
                return getString(KEY_MODEL);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setModel(String model) {
        try {
            put(KEY_MODEL, model);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_MODEL);
        }
    }

    public String getBoard() {
        try {
            if (!isNull(KEY_BOARD)) {
                return getString(KEY_BOARD);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setBoard(String board) {
        try {
            put(KEY_BOARD, board);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_BOARD);
        }
    }

    public String getProduct() {
        try {
            if (!isNull(KEY_PRODUCT)) {
                return getString(KEY_PRODUCT);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setProduct(String product) {
        try {
            put(KEY_PRODUCT, product);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_PRODUCT);
        }
    }

    public String getBrand() {
        try {
            if (!isNull(KEY_BRAND)) {
                return getString(KEY_BRAND);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setBrand(String brand) {
        try {
            put(KEY_BRAND, brand);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_BRAND);
        }
    }

    public String getCpu() {
        try {
            if (!isNull(KEY_CPU)) {
                return getString(KEY_CPU);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setCpu(String cpu) {
        try {
            put(KEY_CPU, cpu);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_CPU);
        }
    }

    public String getDevice() {
        try {
            if (!isNull(KEY_DEVICE)) {
                return getString(KEY_DEVICE);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setDevice(String device) {
        try {
            put(KEY_DEVICE, device);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_DEVICE);
        }
    }

    public String getCarrier() {
        try {
            if (!isNull(KEY_CARRIER)) {
                return getString(KEY_CARRIER);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setCarrier(String carrier) {
        try {
            put(KEY_CARRIER, carrier);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_CARRIER);
        }
    }

    public String getCurrentCarrier() {
        try {
            if (!isNull(KEY_CURRENT_CARRIER)) {
                return getString(KEY_CURRENT_CARRIER);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setCurrentCarrier(String currentCarrier) {
        try {
            put(KEY_CURRENT_CARRIER, currentCarrier);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_CURRENT_CARRIER);
        }
    }

    public String getNetworkType() {
        try {
            if (!isNull(KEY_NETWORK_TYPE)) {
                return getString(KEY_NETWORK_TYPE);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setNetworkType(String networkType) {
        try {
            put(KEY_NETWORK_TYPE, networkType);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_NETWORK_TYPE);
        }
    }

    public String getBuildType() {
        try {
            if (!isNull(KEY_BUILD_TYPE)) {
                return getString(KEY_BUILD_TYPE);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setBuildType(String buildType) {
        try {
            put(KEY_BUILD_TYPE, buildType);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_BUILD_TYPE);
        }
    }

    public String getBuildId() {
        try {
            if (!isNull(KEY_BUILD_ID)) {
                return getString(KEY_BUILD_ID);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setBuildId(String buildId) {
        try {
            put(KEY_BUILD_ID, buildId);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_BUILD_ID);
        }
    }

    public String getBootloaderVersion() {
        try {
            if (!isNull(KEY_BOOTLOADER_VERSION)) {
                return getString(KEY_BOOTLOADER_VERSION);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setBootloaderVersion(String bootloaderVersion) {
        try {
            put(KEY_BOOTLOADER_VERSION, bootloaderVersion);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_BOOTLOADER_VERSION);
        }
    }

    public String getRadioVersion() {
        try {
            if (!isNull(KEY_RADIO_VERSION)) {
                return getString(KEY_RADIO_VERSION);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setRadioVersion(String radioVersion) {
        try {
            put(KEY_RADIO_VERSION, radioVersion);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_RADIO_VERSION);
        }
    }

    @SuppressWarnings("unchecked") // We check it coming in.
    public CustomData getCustomData() {
        if (!isNull(KEY_CUSTOM_DATA)) {
            try {
                return new CustomData(getJSONObject(KEY_CUSTOM_DATA));
            } catch (JSONException e) {
            }
        }
        return null;
    }

    public void setCustomData(CustomData customData) {
        try {
            put(KEY_CUSTOM_DATA, customData);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_CUSTOM_DATA);
        }
    }

    @SuppressWarnings("unchecked") // We check it coming in.
    public CustomData getIntegrationConfig() {
        if (!isNull(KEY_INTEGRATION_CONFIG)) {
            try {
                return new CustomData(getJSONObject(KEY_INTEGRATION_CONFIG));
            } catch (JSONException e) {
            }
        }
        return null;
    }

    public void setIntegrationConfig(CustomData integrationConfig) {
        try {
            put(KEY_INTEGRATION_CONFIG, integrationConfig);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_INTEGRATION_CONFIG);
        }
    }

    public String getLocaleCountryCode() {
        try {
            if (!isNull(KEY_LOCALE_COUNTRY_CODE)) {
                return getString(KEY_LOCALE_COUNTRY_CODE);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setLocaleCountryCode(String localeCountryCode) {
        try {
            put(KEY_LOCALE_COUNTRY_CODE, localeCountryCode);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_LOCALE_COUNTRY_CODE);
        }
    }

    public String getLocaleLanguageCode() {
        try {
            if (!isNull(KEY_LOCALE_LANGUAGE_CODE)) {
                return getString(KEY_LOCALE_LANGUAGE_CODE);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setLocaleLanguageCode(String localeLanguageCode) {
        try {
            put(KEY_LOCALE_LANGUAGE_CODE, localeLanguageCode);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_LOCALE_LANGUAGE_CODE);
        }
    }

    public String getLocaleRaw() {
        try {
            if (!isNull(KEY_LOCALE_RAW)) {
                return getString(KEY_LOCALE_RAW);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setLocaleRaw(String localeRaw) {
        try {
            put(KEY_LOCALE_RAW, localeRaw);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_LOCALE_RAW);
        }
    }

    public String getUtcOffset() {
        try {
            if (!isNull(KEY_UTC_OFFSET)) {
                return getString(KEY_UTC_OFFSET);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public void setUtcOffset(String utcOffset) {
        try {
            put(KEY_UTC_OFFSET, utcOffset);
        } catch (JSONException e) {
            Log.w("Error adding %s to Device.", KEY_UTC_OFFSET);
        }
    }

}
