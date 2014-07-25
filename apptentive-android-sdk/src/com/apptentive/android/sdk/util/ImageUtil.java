/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.apptentive.android.sdk.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sky Kelsey
 */
public class ImageUtil {

    /**
     * From <a href="http://developer.android.com/training/displaying-bitmaps/load-bitmap.html">Loading Large Bitmaps Efficiently</a>
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * This method decodes a bitmap from a file, and does pixel combining in order to produce an in-memory bitmap that is
     * smaller than the original. It will create only the returned bitmap in memory.
     * From <a href="http://developer.android.com/training/displaying-bitmaps/load-bitmap.html">Loading Large Bitmaps Efficiently</a>
     *
     * @param is              An InputStream containing the bytes of an image.
     * @param minShrunkWidth  If edge of this image is greater than minShrunkWidth, the image will be shrunken such it is not smaller than minShrunkWidth.
     * @param minShrunkHeight If edge of this image is greater than minShrunkHeight, the image will be shrunken such it is not smaller than minShrunkHeight.
     * @param config          You can use this to change the number of bytes per pixel using various bitmap configurations.
     * @return A bitmap whose edges are equal to or less than minShrunkEdge in length.
     */
    public static Bitmap createLightweightScaledBitmapFromStream(InputStream is, int minShrunkWidth, int minShrunkHeight, Bitmap.Config config) {

        BufferedInputStream bis = new BufferedInputStream(is, 32 * 1024);
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (config != null) {
                options.inPreferredConfig = config;
            }

            final BitmapFactory.Options decodeBoundsOptions = new BitmapFactory.Options();
            decodeBoundsOptions.inJustDecodeBounds = true;
            bis.mark(Integer.MAX_VALUE);
            BitmapFactory.decodeStream(bis, null, decodeBoundsOptions);
            bis.reset();

            final int width = decodeBoundsOptions.outWidth;
            final int height = decodeBoundsOptions.outHeight;
            Log.v("Original bitmap dimensions: %d x %d", width, height);
            int sampleRatio = Math.max(width / minShrunkWidth, height / minShrunkHeight);
            if (sampleRatio >= 2) {
                options.inSampleSize = sampleRatio;
            }
            Log.v("Bitmap sample size = %d", options.inSampleSize);

            Bitmap ret = BitmapFactory.decodeStream(bis, null, options);
            Log.d("Sampled bitmap size = %d X %d", options.outWidth, options.outHeight);
            return ret;
        } catch (IOException e) {
            Log.e("Error resizing bitmap from InputStream.", e);
        } finally {
            Util.ensureClosed(bis);
        }
        return null;
    }

    /**
     * This method first uses a straight binary pixel conversion to shrink an image to *almost* the right size, and then
     * performs a scaling of this resulting bitmap to achieve the final size. It will create two bitmaps in memory while it
     * is running.
     *
     * @param is        An InputStream from the image file.
     * @param maxWidth  The maximum width to scale this image to, or 0 to ignore this parameter.
     * @param maxHeight The maximum height to scale this image to, or 0 to ignore this parameter.
     * @param config    A Bitmap.Config to apply to the Bitmap as it is read in.
     * @return A Bitmap scaled by maxWidth, maxHeight, and config.
     */
    public static Bitmap createScaledBitmapFromStream(InputStream is, int maxWidth, int maxHeight, Bitmap.Config config) {

        // Start by grabbing the bitmap from file, sampling down a little first if the image is huge.
        Bitmap tempBitmap = createLightweightScaledBitmapFromStream(is, maxWidth, maxHeight, config);

        Bitmap outBitmap = tempBitmap;
        int width = tempBitmap.getWidth();
        int height = tempBitmap.getHeight();

        // Find the greatest ration difference, as this is what we will shrink both sides to.
        float ratio = calculateBitmapScaleFactor(width, height, maxWidth, maxHeight);

        if (ratio < 1.0f) { // Don't blow up small images, only shrink bigger ones.
            int newWidth = (int) (ratio * width);
            int newHeight = (int) (ratio * height);
            Log.v("Scaling image further down to %d x %d", newWidth, newHeight);
            outBitmap = Bitmap.createScaledBitmap(tempBitmap, newWidth, newHeight, true);
            Log.d("Final bitmap dimensions: %d x %d", outBitmap.getWidth(), outBitmap.getHeight());
            tempBitmap.recycle();
        }
        return outBitmap;
    }

    public static float calculateBitmapScaleFactor(int width, int height, int maxWidth, int maxHeight) {
        float widthRatio = maxWidth <= 0 ? 1.0f : (float) maxWidth / width;
        float heightRatio = maxHeight <= 0 ? 1.0f : (float) maxHeight / height;
        return Math.min(1.0f, Math.min(widthRatio, heightRatio)); // Don't scale above 1.0x
    }
}
