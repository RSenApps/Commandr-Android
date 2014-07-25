/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.model.FileMessage;
import com.apptentive.android.sdk.model.StoredFile;
import com.apptentive.android.sdk.module.metric.MetricModule;
import com.apptentive.android.sdk.util.ImageUtil;
import com.apptentive.android.sdk.util.Util;

import java.io.FileInputStream;

/**
 * @author Sky Kelsey
 */
public class FileMessageView extends PersonalMessageView<FileMessage> {

    // Some limits to keep images from being bigger than their display area.
    private final static float MAX_IMAGE_SCREEN_PROPORTION_X = 0.5f;
    private final static float MAX_IMAGE_SCREEN_PROPORTION_Y = 0.6f;

    // Some absolute size limits to keep bitmap sizes down.
    private final static int MAX_IMAGE_DISPLAY_WIDTH = 800;
    private final static int MAX_IMAGE_DISPLAY_HEIGHT = 800;

    public FileMessageView(Context context, FileMessage message) {
        super(context, message);
    }

    protected void init(FileMessage message) {
        super.init(message);
        LayoutInflater inflater = LayoutInflater.from(context);
        FrameLayout bodyLayout = (FrameLayout) findViewById(R.id.apptentive_message_body);
        inflater.inflate(R.layout.apptentive_message_body_file, bodyLayout);
    }

    public void updateMessage(final FileMessage newMessage) {
        FileMessage oldMessage = message;
        super.updateMessage(newMessage);

        if (newMessage == null) {
            return;
        }
        StoredFile storedFile = newMessage.getStoredFile(context);
        if (storedFile == null || storedFile.getLocalFilePath() == null) {
            return;
        }

        StoredFile oldStoredFile = null;
        if (oldMessage != null) {
            oldStoredFile = oldMessage.getStoredFile(context);
        }

        boolean hasNoOldFilePath = oldMessage == null || oldStoredFile.getLocalFilePath() == null;
        boolean pathDiffers = oldMessage != null && !storedFile.getLocalFilePath().equals(oldStoredFile.getLocalFilePath());
        if (hasNoOldFilePath || pathDiffers) {
            // TODO: Figure out a way to group into classes by mime type (image, text, other).
            String mimeType = storedFile.getMimeType();

            if (mimeType == null) {
                Log.e("FileMessage mime type is null.");
                return;
            }

            ImageView imageView = (ImageView) findViewById(R.id.apptentive_file_message_image);
            if (mimeType.contains("image")) {
                imageView.setVisibility(View.INVISIBLE);

                Point dimensions = getBitmapDimensions(storedFile);
                if (dimensions == null) {
                    Log.w("Unable to peek at image dimensions.");
                    return;
                }
                imageView.setPadding(dimensions.x, dimensions.y, 0, 0);
                loadImage(storedFile, imageView);
            }
        }
    }

    /**
     * This method will load a bitmap from the StoredFile on another thread, and then update the ImageView with the
     * resulting bitmap on the UI thread.
     *
     * @param storedFile The file to pull the bitmap from.
     * @param imageView  The ImageView to insert the bitmap into.
     */
    private void loadImage(final StoredFile storedFile, final ImageView imageView) {
        Thread thread = new Thread() {
            public void run() {
                FileInputStream fis = null;
                final Bitmap imageBitmap;
                try {
                    fis = context.openFileInput(storedFile.getLocalFilePath());
                    Point point = Util.getScreenSize(context);
                    int maxImageWidth = (int) (MAX_IMAGE_SCREEN_PROPORTION_X * point.x);
                    int maxImageHeight = (int) (MAX_IMAGE_SCREEN_PROPORTION_Y * point.x);
                    maxImageWidth = maxImageWidth > MAX_IMAGE_DISPLAY_WIDTH ? MAX_IMAGE_DISPLAY_WIDTH : maxImageWidth;
                    maxImageHeight = maxImageHeight > MAX_IMAGE_DISPLAY_HEIGHT ? MAX_IMAGE_DISPLAY_HEIGHT : maxImageHeight;
                    imageBitmap = ImageUtil.createScaledBitmapFromStream(fis, maxImageWidth, maxImageHeight, null);
                    Log.v("Loaded bitmap and resized to: %d x %d", imageBitmap.getWidth(), imageBitmap.getHeight());
                    imageView.post(new Runnable() {
                        public void run() {
                            imageView.setImageBitmap(imageBitmap);
                            imageView.setPadding(0, 0, 0, 0);
                            imageView.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (Exception e) {
                    Log.e("Error opening stored image.", e);
                } catch (OutOfMemoryError e) {
                    // It's generally not a good idea to catch an OOME. But in this case, the OOME had to result from allocating a bitmap,
                    // So the system should be in a good state.
                    // TODO: Log an event to the server so we know an OOME occurred.
                    Log.e("Ran out of memory opening image.", e);
                } finally {
                    Util.ensureClosed(fis);
                }
            }
        };
        Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.w("UncaughtException in FileMessageView.", throwable);
                MetricModule.sendError(context.getApplicationContext(), throwable, null, null);
            }
        };
        thread.setUncaughtExceptionHandler(handler);
        thread.setName("Apptentive-FileMessageViewLoadImage");
        thread.start();
    }

    private Point getBitmapDimensions(StoredFile storedFile) {
        Point ret = null;
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(storedFile.getLocalFilePath());

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(fis, null, options);

            Point point = Util.getScreenSize(context);
            int maxImageWidth = (int) (MAX_IMAGE_SCREEN_PROPORTION_X * point.x);
            int maxImageHeight = (int) (MAX_IMAGE_SCREEN_PROPORTION_Y * point.x);
            maxImageWidth = maxImageWidth > MAX_IMAGE_DISPLAY_WIDTH ? MAX_IMAGE_DISPLAY_WIDTH : maxImageWidth;
            maxImageHeight = maxImageHeight > MAX_IMAGE_DISPLAY_HEIGHT ? MAX_IMAGE_DISPLAY_HEIGHT : maxImageHeight;
            float scale = ImageUtil.calculateBitmapScaleFactor(options.outWidth, options.outHeight, maxImageWidth, maxImageHeight);
            ret = new Point((int) (scale * options.outWidth), (int) (scale * options.outHeight));
        } catch (Exception e) {
            Log.e("Error opening stored file.", e);
        } finally {
            Util.ensureClosed(fis);
        }
        return ret;
    }
}
