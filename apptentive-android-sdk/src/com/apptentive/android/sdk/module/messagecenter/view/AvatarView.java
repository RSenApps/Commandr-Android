/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.View;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.util.Util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Sky Kelsey
 *         TODO: Save the final bitmap so it's not constructed each time onDraw is called.
 */
public class AvatarView extends View {

    private final static int RADIUS_DIPS = 7;
    private static float radius;
    Bitmap avatar;

    public AvatarView(Context context, String urlString) {
        super(context);
        this.setBackgroundColor(Color.TRANSPARENT);
        radius = Util.dipsToPixelsFloat(context, RADIUS_DIPS);
        try {
            URL url = new URL(urlString);
            avatar = BitmapFactory.decodeStream(url.openStream());
        } catch (MalformedURLException e) {
            // Bad url, just use default image.
        } catch (IOException e) {
            Log.d("Error opening avatar from URL: \"%s\"", e, urlString);
        } finally {
            if (avatar == null) {
                avatar = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth() + 1;
        int height = getHeight() + 1;
        RectF rect = new RectF(1, 1, width - 1, height - 1);

        Matrix matrix = new Matrix();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.YELLOW);

        Bitmap duplicate = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas maskCanvas = new Canvas(mask);
        maskCanvas.drawRoundRect(rect, radius, radius, paint);

        matrix.setScale((float) width / avatar.getWidth(), (float) height / avatar.getHeight());

        Canvas dupCanvas = new Canvas(duplicate);
        dupCanvas.drawBitmap(avatar, matrix, null);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        dupCanvas.drawBitmap(mask, 0, 0, paint);

        canvas.drawBitmap(duplicate, 0, 0, null);
    }
}
