/*
 * Copyright (c) 2011, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.apptentive.android.sdk.R;

/**
 * @author Sky Kelsey
 */
public class PaperBackground extends View {
    public PaperBackground(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth() + 1;
        int height = getHeight() + 1;
        Rect size = new Rect(0, 0, width, height);
        RectF sizef = new RectF(0, 0, width, height);

        // Use masking
        Bitmap rounder = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas paperCanvas = new Canvas(rounder);
        Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xferPaint.setColor(Color.RED);
        paperCanvas.drawRoundRect(sizef, 15.0f, 15.0f, xferPaint);
        xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));


        // Or use clipping... decided to use masking instead because clipping doesn't antialias edges.
/*
        Path clip = new Path();
		clip.addRoundRect(sizef, 10.0f, 10.0f, Path.Direction.CW);
		canvas.clipPath(clip);
*/

        BitmapDrawable paperDrawable = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.apptentive_paper_bg));
        paperDrawable.setBounds(size);
        paperDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paperDrawable.draw(canvas);

        canvas.drawBitmap(rounder, 0, 0, xferPaint);
    }
}
