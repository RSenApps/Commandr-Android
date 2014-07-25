/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter.view;

import android.content.Context;
import android.widget.FrameLayout;

import com.apptentive.android.sdk.model.Event;
import com.apptentive.android.sdk.model.Message;
import com.apptentive.android.sdk.module.messagecenter.MessageManager;
import com.apptentive.android.sdk.module.metric.MetricModule;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sky Kelsey
 */
public abstract class MessageView<T extends Message> extends FrameLayout {

    protected Context context;
    protected T message;

    public MessageView(final Context context, final T message) {
        super(context);
        this.context = context;
        init(message);
        updateMessage(message);
        if (!message.isRead()) {
            message.setRead(true);
            Map<String, String> data = new HashMap<String, String>();
            data.put("message_id", message.getId());
            MetricModule.sendMetric(context, Event.EventLabel.message_center__read, null, data);
            post(new Runnable() {
                public void run() {
                    MessageManager.updateMessage(context, message);
                    MessageManager.notifyHostUnreadMessagesListener(MessageManager.getUnreadMessageCount(context));
                }
            });
        }
    }

    protected void init(T message) {
    }

    public void updateMessage(T newMessage) {
        message = newMessage;
    }
}
