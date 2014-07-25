/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.comm;

import android.content.Context;

import com.apptentive.android.sdk.GlobalInfo;
import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.model.AppRelease;
import com.apptentive.android.sdk.model.ConversationTokenRequest;
import com.apptentive.android.sdk.model.Device;
import com.apptentive.android.sdk.model.Event;
import com.apptentive.android.sdk.model.FileMessage;
import com.apptentive.android.sdk.model.Message;
import com.apptentive.android.sdk.model.Person;
import com.apptentive.android.sdk.model.Sdk;
import com.apptentive.android.sdk.model.StoredFile;
import com.apptentive.android.sdk.model.SurveyResponse;
import com.apptentive.android.sdk.util.Constants;
import com.apptentive.android.sdk.util.Util;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * TODO: When we drop API level 7 (2.1) support, we can start using AndroidHttpClient.
 * http://developer.android.com/reference/android/net/http/AndroidHttpClient.html
 *
 * @author Sky Kelsey
 */
public class ApptentiveClient {

    // TODO: Break out a version for each endpoint if we start to version endpoints separately.
    private static final String API_VERSION = "1";

    private static final String USER_AGENT_STRING = "Apptentive/%s (Android)"; // Format with SDK version string.

    private static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 30000;
    private static final int DEFAULT_HTTP_SOCKET_TIMEOUT = 30000;

    // Active API
    private static final String ENDPOINT_BASE = "https://api.apptentive.com";
    private static final String ENDPOINT_CONVERSATION = ENDPOINT_BASE + "/conversation";
    private static final String ENDPOINT_CONVERSATION_FETCH = ENDPOINT_CONVERSATION + "?count=%s&after_id=%s&before_id=%s";
    private static final String ENDPOINT_CONFIGURATION = ENDPOINT_CONVERSATION + "/configuration";
    private static final String ENDPOINT_MESSAGES = ENDPOINT_BASE + "/messages";
    private static final String ENDPOINT_EVENTS = ENDPOINT_BASE + "/events";
    private static final String ENDPOINT_DEVICES = ENDPOINT_BASE + "/devices";
    private static final String ENDPOINT_PEOPLE = ENDPOINT_BASE + "/people";
    private static final String ENDPOINT_SURVEYS_POST = ENDPOINT_BASE + "/surveys/%s/respond";

    private static final String ENDPOINT_INTERACTIONS = ENDPOINT_BASE + "/interactions";

    // Deprecated API
    // private static final String ENDPOINT_RECORDS = ENDPOINT_BASE + "/records";
    // private static final String ENDPOINT_SURVEYS_FETCH = ENDPOINT_BASE + "/surveys";


    public static ApptentiveHttpResponse getConversationToken(ConversationTokenRequest conversationTokenRequest) {
        return performHttpRequest(GlobalInfo.apiKey, ENDPOINT_CONVERSATION, Method.POST, conversationTokenRequest.toString());
    }

    public static ApptentiveHttpResponse getAppConfiguration() {
        return performHttpRequest(GlobalInfo.conversationToken, ENDPOINT_CONFIGURATION, Method.GET, null);
    }

    /**
     * Gets all messages since the message specified by guid was sent.
     *
     * @return An ApptentiveHttpResponse object with the HTTP response code, reason, and content.
     */
    public static ApptentiveHttpResponse getMessages(Integer count, String afterId, String beforeId) {
        String uri = String.format(ENDPOINT_CONVERSATION_FETCH, count == null ? "" : count.toString(), afterId == null ? "" : afterId, beforeId == null ? "" : beforeId);
        return performHttpRequest(GlobalInfo.conversationToken, uri, Method.GET, null);
    }

    public static ApptentiveHttpResponse postMessage(Context context, Message message) {
        switch (message.getType()) {
            case TextMessage:
                return performHttpRequest(GlobalInfo.conversationToken, ENDPOINT_MESSAGES, Method.POST, message.marshallForSending());
            case AutomatedMessage:
                return performHttpRequest(GlobalInfo.conversationToken, ENDPOINT_MESSAGES, Method.POST, message.marshallForSending());
            case FileMessage:
                FileMessage fileMessage = (FileMessage) message;
                StoredFile storedFile = fileMessage.getStoredFile(context);
                return performMultipartFilePost(context, GlobalInfo.conversationToken, ENDPOINT_MESSAGES, message.marshallForSending(), storedFile);
            case unknown:
                break;
        }
        return new ApptentiveHttpResponse();
    }

    public static ApptentiveHttpResponse postEvent(Event event) {
        return performHttpRequest(GlobalInfo.conversationToken, ENDPOINT_EVENTS, Method.POST, event.marshallForSending());
    }

    public static ApptentiveHttpResponse putDevice(Device device) {
        return performHttpRequest(GlobalInfo.conversationToken, ENDPOINT_DEVICES, Method.PUT, device.marshallForSending());
    }

    public static ApptentiveHttpResponse putSdk(Sdk sdk) {
        return performHttpRequest(GlobalInfo.conversationToken, ENDPOINT_CONVERSATION, Method.PUT, sdk.marshallForSending());
    }

    public static ApptentiveHttpResponse putAppRelease(AppRelease appRelease) {
        return performHttpRequest(GlobalInfo.conversationToken, ENDPOINT_CONVERSATION, Method.PUT, appRelease.marshallForSending());
    }

    public static ApptentiveHttpResponse putPerson(Person person) {
        return performHttpRequest(GlobalInfo.conversationToken, ENDPOINT_PEOPLE, Method.PUT, person.marshallForSending());
    }

    public static ApptentiveHttpResponse postSurvey(SurveyResponse survey) {
        String endpoint = String.format(ENDPOINT_SURVEYS_POST, survey.getId());
        return performHttpRequest(GlobalInfo.conversationToken, endpoint, Method.POST, survey.marshallForSending());
    }

    public static ApptentiveHttpResponse getInteractions() {
        return performHttpRequest(GlobalInfo.conversationToken, ENDPOINT_INTERACTIONS, Method.GET, null);
    }

    private static ApptentiveHttpResponse performHttpRequest(String oauthToken, String uri, Method method, String body) {
        Log.d("Performing request to %s", uri);
        //Log.e("OAUTH Token: %s", oauthToken);

        ApptentiveHttpResponse ret = new ApptentiveHttpResponse();
        HttpClient httpClient = null;
        try {
            HttpRequestBase request;
            httpClient = new DefaultHttpClient();
            switch (method) {
                case GET:
                    request = new HttpGet(uri);
                    break;
                case PUT:
                    request = new HttpPut(uri);
                    request.setHeader("Content-Type", "application/json");
                    Log.d("PUT body: " + body);
                    ((HttpPut) request).setEntity(new StringEntity(body, "UTF-8"));
                    break;
                case POST:
                    request = new HttpPost(uri);
                    request.setHeader("Content-Type", "application/json");
                    Log.d("POST body: " + body);
                    ((HttpPost) request).setEntity(new StringEntity(body, "UTF-8"));
                    break;
                default:
                    Log.e("Unrecognized method: " + method.name());
                    return ret;
            }

            HttpParams httpParams = request.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_HTTP_CONNECT_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_HTTP_SOCKET_TIMEOUT);
            httpParams.setParameter("http.useragent", getUserAgentString());
            request.setHeader("Authorization", "OAuth " + oauthToken);
            request.setHeader("Accept", "application/json");
            request.setHeader("X-API-Version", API_VERSION);

            HttpResponse response = httpClient.execute(request);
            int code = response.getStatusLine().getStatusCode();
            ret.setCode(code);
            ret.setReason(response.getStatusLine().getReasonPhrase());
            Log.d("Response Status Line: " + response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                ret.setContent(EntityUtils.toString(entity, "UTF-8"));
                if (code >= 200 && code < 300) {
                    Log.d("Response: " + ret.getContent());
                } else {
                    Log.w("Response: " + ret.getContent());
                }
            }
            HeaderIterator headerIterator = response.headerIterator();
            if (headerIterator != null) {
                Map<String, String> headers = new HashMap<String, String>();
                while (headerIterator.hasNext()) {
                    Header header = (Header) headerIterator.next();
                    headers.put(header.getName(), header.getValue());
                    //Log.v("Header: %s = %s", header.getName(), header.getValue());
                }
                ret.setHeaders(headers);
            }
        } catch (IllegalArgumentException e) {
            Log.w("Error communicating with server.", e);
        } catch (SocketTimeoutException e) {
            Log.w("Timeout communicating with server.");
        } catch (IOException e) {
            Log.w("Error communicating with server.", e);
        } finally {
            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }
        return ret;
    }

    private static ApptentiveHttpResponse performMultipartFilePost(Context context, String oauthToken, String uri, String postBody, StoredFile storedFile) {
        Log.d("Performing multipart request to %s", uri);

        ApptentiveHttpResponse ret = new ApptentiveHttpResponse();

        if (storedFile == null) {
            Log.e("StoredFile is null. Unable to send.");
            return ret;
        }

        int bytesRead;
        int bufferSize = 4096;
        byte[] buffer;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = UUID.randomUUID().toString();

        HttpURLConnection connection;
        DataOutputStream os = null;
        InputStream is = null;

        try {
            is = context.openFileInput(storedFile.getLocalFilePath());

            // Set up the request.
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_HTTP_SOCKET_TIMEOUT);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("Authorization", "OAuth " + oauthToken);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-API-Version", API_VERSION);
            connection.setRequestProperty("User-Agent", getUserAgentString());

            StringBuilder requestText = new StringBuilder();

            // Write form data
            requestText.append(twoHyphens).append(boundary).append(lineEnd);
            requestText.append("Content-Disposition: form-data; name=\"message\"").append(lineEnd);
            requestText.append("Content-Type: text/plain").append(lineEnd);
            requestText.append(lineEnd);
            requestText.append(postBody);
            requestText.append(lineEnd);

            // Write file attributes.
            requestText.append(twoHyphens).append(boundary).append(lineEnd);
            requestText.append("Content-Disposition: form-data; name=\"file\"; filename=\"file.png\"").append(lineEnd);
            requestText.append("Content-Type: ").append(storedFile.getMimeType()).append(lineEnd);
            requestText.append(lineEnd);

            Log.d("Post body: " + requestText);

            // Open an output stream.
            os = new DataOutputStream(connection.getOutputStream());

            // Write the text so far.
            os.writeBytes(requestText.toString());

            try {
                // Write the actual file.
                buffer = new byte[bufferSize];
                while ((bytesRead = is.read(buffer, 0, bufferSize)) > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                Log.d("Error writing file bytes to HTTP connection.", e);
                ret.setBadPayload(true);
                throw e;
            }

            os.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            os.close();

            ret.setCode(connection.getResponseCode());
            ret.setReason(connection.getResponseMessage());

            // TODO: These streams may not be ready to read now. Put this in a new thread.
            // Read the normal response.
            InputStream nis = null;
            ByteArrayOutputStream nbaos = null;
            try {
                Log.d("Sending file: " + storedFile.getLocalFilePath());
                nis = connection.getInputStream();
                nbaos = new ByteArrayOutputStream();
                byte[] eBuf = new byte[1024];
                int eRead;
                while (nis != null && (eRead = nis.read(eBuf, 0, 1024)) > 0) {
                    nbaos.write(eBuf, 0, eRead);
                }
                ret.setContent(nbaos.toString());
            } catch (IOException e) {
                Log.w("Can't read return stream.", e);
            } finally {
                Util.ensureClosed(nis);
                Util.ensureClosed(nbaos);
            }

            // Read the error response.
            InputStream eis = null;
            ByteArrayOutputStream ebaos = null;
            try {
                eis = connection.getErrorStream();
                ebaos = new ByteArrayOutputStream();
                byte[] eBuf = new byte[1024];
                int eRead;
                while (eis != null && (eRead = eis.read(eBuf, 0, 1024)) > 0) {
                    ebaos.write(eBuf, 0, eRead);
                }
                if (ebaos.size() > 0) {
                    ret.setContent(ebaos.toString());
                }
            } catch (IOException e) {
                Log.w("Can't read error stream.", e);
            } finally {
                Util.ensureClosed(eis);
                Util.ensureClosed(ebaos);
            }

            Log.d("HTTP " + connection.getResponseCode() + ": " + connection.getResponseMessage() + "");
            Log.v(ret.getContent());
        } catch (FileNotFoundException e) {
            Log.e("Error getting file to upload.", e);
        } catch (MalformedURLException e) {
            Log.e("Error constructing url for file upload.", e);
        } catch (SocketTimeoutException e) {
            Log.w("Timeout communicating with server.");
        } catch (IOException e) {
            Log.e("Error executing file upload.", e);
        } finally {
            Util.ensureClosed(is);
            Util.ensureClosed(os);
        }
        return ret;
    }

    private static String getUserAgentString() {
        return String.format(USER_AGENT_STRING, Constants.APPTENTIVE_SDK_VERSION);
    }

    private enum Method {
        GET,
        PUT,
        POST
    }
}
