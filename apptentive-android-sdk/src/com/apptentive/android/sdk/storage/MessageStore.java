/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.storage;

import com.apptentive.android.sdk.model.Message;

import java.util.List;

/**
 * @author Sky Kelsey
 */
public interface MessageStore extends PayloadStore {

    public void addOrUpdateMessages(Message... message);

    public void updateMessage(Message message);

    public List<Message> getAllMessages();

    public String getLastReceivedMessageId();

    public int getUnreadMessageCount();

    public void deleteAllMessages();

    public void deleteMessage(String nonce);
}
