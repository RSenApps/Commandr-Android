/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter;

/**
 * This interface allows you to receive a notification when the number of unread messages changes.
 *
 * @author Sky Kelsey
 */
public interface UnreadMessagesListener {
    /**
     * This method is called if the number changes, increasing or decreasing. Therefore, it will be called when new
     * messages are available to be viewed, as well as when a message is viewed for the first time. You can use the count
     * returned to decorate a view with a badge indicating how many unread messages are waiting to be seen.
     *
     * @param unreadMessages The total number of unread messages waiting to be viewed by the user.
     */
    public void onUnreadMessageCountChanged(int unreadMessages);
}
