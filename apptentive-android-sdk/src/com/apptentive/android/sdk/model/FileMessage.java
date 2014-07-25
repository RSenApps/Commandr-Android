/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.model;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.storage.ApptentiveDatabase;
import com.apptentive.android.sdk.storage.FileStore;
import com.apptentive.android.sdk.util.CountingOutputStream;
import com.apptentive.android.sdk.util.ImageUtil;
import com.apptentive.android.sdk.util.Util;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sky Kelsey
 */
public class FileMessage extends Message {

    private static final int MAX_STORED_IMAGE_EDGE = 1024;

    private static final String KEY_FILE_NAME = "file_name";
    private static final String KEY_MIME_TYPE = "mime_type";

    public FileMessage() {
        super();
    }

    public FileMessage(String json) throws JSONException {
        super(json);
    }

    @Override
    protected void initType() {
        setType(Type.FileMessage);
    }

    /**
     * FileMessages are sent using a multipart form encoded request, so they are handled differently here.
     *
     * @return A String containing just the meta data about the FileMessage.
     */
    @Override
    public String marshallForSending() {
        return toString();
    }

    public String getFileName() {
        try {
            return getString(KEY_FILE_NAME);
        } catch (JSONException e) {
        }
        return null;
    }

    public void setFileName(String fileName) {
        try {
            put(KEY_FILE_NAME, fileName);
        } catch (JSONException e) {
            Log.e("Unable to set file name.");
        }
    }

    public String getMimeType() {
        try {
            return getString(KEY_MIME_TYPE);
        } catch (JSONException e) {
        }
        return null;
    }

    public void setMimeType(String mimeType) {
        try {
            put(KEY_MIME_TYPE, mimeType);
        } catch (JSONException e) {
            Log.e("Unable to set mime type.");
        }
    }

    private String getStoredFileId() {
        return "apptentive-file-" + getNonce();
    }

    /**
     * This method stores an image, and compresses it in the process so it doesn't fill up the disk. Therefore, do not use
     * it to store an exact copy of the file in question.
     */
    public boolean internalCreateStoredImage(Context context, String uriString) {
        Uri uri = Uri.parse(uriString);

        ContentResolver resolver = context.getContentResolver();
        String mimeType = resolver.getType(uri);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = mime.getExtensionFromMimeType(mimeType);
        setFileName(uri.getLastPathSegment() + "." + extension);
        setMimeType(mimeType);

        // Create a file to save locally.
        String localFileName = getStoredFileId();
        File localFile = new File(localFileName);

        // Copy the file contents over.
        InputStream is = null;
        CountingOutputStream cos = null;
        try {
            is = new BufferedInputStream(context.getContentResolver().openInputStream(uri));
            cos = new CountingOutputStream(new BufferedOutputStream(context.openFileOutput(localFile.getPath(), Context.MODE_PRIVATE)));
            System.gc();
            Bitmap smaller = ImageUtil.createScaledBitmapFromStream(is, MAX_STORED_IMAGE_EDGE, MAX_STORED_IMAGE_EDGE, null);
            // TODO: Is JPEG what we want here?
            smaller.compress(Bitmap.CompressFormat.JPEG, 95, cos);
            cos.flush();
            Log.d("Bitmap saved, size = " + (cos.getBytesWritten() / 1024) + "k");
            smaller.recycle();
            System.gc();
        } catch (FileNotFoundException e) {
            Log.e("File not found while storing image.", e);
            return false;
        } catch (Exception e) {
            Log.a("Error storing image.", e);
            return false;
        } finally {
            Util.ensureClosed(is);
            Util.ensureClosed(cos);
        }

        // Create a StoredFile database entry for this locally saved file.
        StoredFile storedFile = new StoredFile();
        storedFile.setId(getStoredFileId());
        storedFile.setOriginalUri(uri.toString());
        storedFile.setLocalFilePath(localFile.getPath());
        storedFile.setMimeType(mimeType);
        FileStore db = ApptentiveDatabase.getInstance(context);
        return db.putStoredFile(storedFile);
    }

    public boolean createStoredFile(Context context, String uriString) {
        Uri uri = Uri.parse(uriString);

        ContentResolver resolver = context.getContentResolver();
        String mimeType = resolver.getType(uri);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = mime.getExtensionFromMimeType(mimeType);
        setFileName(uri.getLastPathSegment() + "." + extension);
        setMimeType(mimeType);

        InputStream is = null;
        try {
            is = new BufferedInputStream(context.getContentResolver().openInputStream(uri));
            return createStoredFile(context, is, mimeType);
        } catch (FileNotFoundException e) {
            Log.e("File not found while storing file.", e);
        } catch (IOException e) {
            Log.a("Error storing image.", e);
        } finally {
            Util.ensureClosed(is);
        }
        return false;
    }

    public boolean createStoredFile(Context context, byte[] content, String mimeType) {
        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(content);
            return createStoredFile(context, is, mimeType);
        } catch (FileNotFoundException e) {
            Log.e("File not found while storing file.", e);
        } catch (IOException e) {
            Log.a("Error storing file.", e);
        } finally {
            Util.ensureClosed(is);
        }
        return false;
    }

    public boolean createStoredFile(Context context, InputStream is, String mimeType) throws IOException {
        setMimeType(mimeType);

        // Create a file to save locally.
        String localFileName = getStoredFileId();
        File localFile = new File(localFileName);

        // Copy the file contents over.
        CountingOutputStream os = null;
        try {
            os = new CountingOutputStream(new BufferedOutputStream(context.openFileOutput(localFile.getPath(), Context.MODE_PRIVATE)));
            byte[] buf = new byte[2048];
            int count = 0;
            while ((count = is.read(buf, 0, 2048)) != -1) {
                os.write(buf, 0, count);
            }
            Log.d("File saved, size = " + (os.getBytesWritten() / 1024) + "k");
        } finally {
            Util.ensureClosed(os);
        }

        // Create a StoredFile database entry for this locally saved file.
        StoredFile storedFile = new StoredFile();
        storedFile.setId(getStoredFileId());
        storedFile.setLocalFilePath(localFile.getPath());
        storedFile.setMimeType(mimeType);
        FileStore db = ApptentiveDatabase.getInstance(context);
        return db.putStoredFile(storedFile);
    }

    public StoredFile getStoredFile(Context context) {
        FileStore fileStore = ApptentiveDatabase.getInstance(context);
        StoredFile storedFile = fileStore.getStoredFile(getStoredFileId());
        return storedFile;
    }

    public void deleteStoredFile(Context context) {
        FileStore fileStore = ApptentiveDatabase.getInstance(context);
        fileStore.deleteStoredFile(getStoredFileId());
    }
}
