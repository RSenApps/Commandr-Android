/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.model;

/**
 * @author Sky Kelsey
 */
public class StoredFile {
    private String id;
    private String mimeType;
    private String originalUri;
    private String localFilePath;
    private String apptentiveUri;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getOriginalUri() {
        return originalUri;
    }

    public void setOriginalUri(String originalUri) {
        this.originalUri = originalUri;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getApptentiveUri() {
        return apptentiveUri;
    }

    public void setApptentiveUri(String apptentiveUri) {
        this.apptentiveUri = apptentiveUri;
    }
}
