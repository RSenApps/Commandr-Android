/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.comm;

import java.util.Map;

/**
 * @author Sky Kelsey
 */
public class ApptentiveHttpResponse {
    private String content;
    private String reason;
    private Map<String, String> headers;
    private int code;
    private boolean badPayload;

    public ApptentiveHttpResponse() {
        content = null;
        reason = null;
        code = -1;
        badPayload = false;
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

    public boolean isRejectedPermanently() {
        return code >= 400 && code < 500;
    }

    public boolean isRejectedTemporarily() {
        return !(isSuccessful() || isRejectedPermanently());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isBadPayload() {
        return badPayload;
    }

    public void setBadPayload(boolean badPayload) {
        this.badPayload = badPayload;
    }
}
