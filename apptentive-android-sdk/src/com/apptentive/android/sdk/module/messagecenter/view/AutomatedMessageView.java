/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.model.AutomatedMessage;

/**
 * @author Sky Kelsey
 */
public class AutomatedMessageView extends MessageView<AutomatedMessage> {

    public AutomatedMessageView(Context context, AutomatedMessage message) {
        super(context, message);
    }

    protected void init(AutomatedMessage message) {
        super.init(message);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.apptentive_message_auto, this);
        LinearLayout frame = (LinearLayout) findViewById(R.id.apptentive_message_auto_frame);
        frame.setBackgroundDrawable(new ZeroMinSizeDrawable(context.getResources(), R.drawable.apptentive_paper_bg));
    }

    public void updateMessage(final AutomatedMessage newMessage) {
        TextView title = (TextView) findViewById(R.id.apptentive_message_auto_title);
        title.setText(newMessage.getTitle());
        TextView body = (TextView) findViewById(R.id.apptentive_message_auto_body);
        body.setText(newMessage.getBody());
    }
}
