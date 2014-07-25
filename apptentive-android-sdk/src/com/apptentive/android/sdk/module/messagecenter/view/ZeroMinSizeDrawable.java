/*
 * Copyright (c) 2012, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter.view;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;

/**
 * This class is a hack that lets you tile a bitmap that is actually larger in either dimension than the view it will
 * tile into.
 *
 * @author Sky Kelsey
 */
public class ZeroMinSizeDrawable extends BitmapDrawable {

    public ZeroMinSizeDrawable(Resources res, int resId) {
        super(res, ((BitmapDrawable) res.getDrawable(resId)).getBitmap());
    }

    public ZeroMinSizeDrawable(Resources res, BitmapDrawable drawable) {
        super(res, drawable.getBitmap());
    }

    @Override
    public int getMinimumWidth() {
        return 0;
    }

    @Override
    public int getMinimumHeight() {
        return 0;
    }
}
