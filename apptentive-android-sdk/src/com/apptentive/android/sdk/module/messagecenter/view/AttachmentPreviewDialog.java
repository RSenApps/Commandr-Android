/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.module.rating.view.ApptentiveBaseDialog;
import com.apptentive.android.sdk.util.ImageUtil;
import com.apptentive.android.sdk.util.Util;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Sky Kelsey
 */
public class AttachmentPreviewDialog extends ApptentiveBaseDialog {

    private OnAttachmentAcceptedListener listener;

    public AttachmentPreviewDialog(Context context) {
        super(context, R.layout.apptentive_message_center_attachment_preview);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button no = (Button) findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button yes = (Button) findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AttachmentPreviewDialog.this.listener != null) {
                    listener.onAttachmentAccepted();
                    dismiss();
                }
            }
        });
    }

    public void setImage(Uri data) {
        ImageView image = (ImageView) findViewById(R.id.image);
        // Show a thumbnail version of the image.
        InputStream is = null;
        final Bitmap thumbnail;
        try {
            is = getContext().getContentResolver().openInputStream(data);
            thumbnail = ImageUtil.createLightweightScaledBitmapFromStream(is, 200, 300, null);
        } catch (FileNotFoundException e) {
            // TODO: Error toast?
            return;
        } finally {
            Util.ensureClosed(is);
        }

        if (thumbnail == null) {
            return;
        }

        image.setImageBitmap(thumbnail);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                thumbnail.recycle();
                System.gc();
            }
        });
    }


    public void setOnAttachmentAcceptedListener(OnAttachmentAcceptedListener listener) {
        this.listener = listener;
    }

    public interface OnAttachmentAcceptedListener {
        public void onAttachmentAccepted();
    }
}
