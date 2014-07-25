/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.model.AutomatedMessage;
import com.apptentive.android.sdk.model.FileMessage;
import com.apptentive.android.sdk.model.Message;
import com.apptentive.android.sdk.model.Payload;
import com.apptentive.android.sdk.model.TextMessage;

/**
 * @author Sky Kelsey
 */
public class MessageAdapter<T extends Message> extends ArrayAdapter<T> {

    public MessageAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);
        if (message.getBaseType() == Payload.BaseType.message) {
            switch (message.getType()) {
                case TextMessage:
                    return new TextMessageView(parent.getContext(), (TextMessage) message);
                case FileMessage:
                    return new FileMessageView(parent.getContext(), (FileMessage) message);
                case AutomatedMessage:
                    return new AutomatedMessageView(parent.getContext(), (AutomatedMessage) message);
                default:
                    Log.a("Unrecognized message type: %s", message.getType());
                    return null;
            }
        }
        Log.d("Can't render non-Message Payload as Message: %s", message.getType());
        return null;

    }
}
