package com.RSen.Commandr.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.telephony.SmsMessage.MessageClass;
import android.text.format.DateUtils;

public class SmsMmsMessage {
    // Message types
    public static final int MESSAGE_TYPE_SMS = 0;
    public static final int MESSAGE_TYPE_MMS = 1;
    public static final int MESSAGE_TYPE_MESSAGE = 2;
    // Timestamp compare buffer for incoming messages
    public static final int MESSAGE_COMPARE_TIME_BUFFER = 5000; // 5 seconds
    // Private EXTRAS strings
    private static final String PREFIX = "net.everythingandroid.smspopup.";
    private static final String EXTRAS_FROM_ADDRESS = PREFIX + "EXTRAS_FROM_ADDRESS";
    private static final String EXTRAS_MESSAGE_BODY = PREFIX + "EXTRAS_MESSAGE_BODY";
    private static final String EXTRAS_TIMESTAMP = PREFIX + "EXTRAS_TIMESTAMP";
    private static final String EXTRAS_UNREAD_COUNT = PREFIX + "EXTRAS_UNREAD_COUNT";
    private static final String EXTRAS_THREAD_ID = PREFIX + "EXTRAS_THREAD_ID";
    private static final String EXTRAS_CONTACT_ID = PREFIX + "EXTRAS_CONTACT_ID";
    private static final String EXTRAS_CONTACT_LOOKUP = PREFIX + "EXTRAS_CONTACT_LOOKUP";
    private static final String EXTRAS_CONTACT_NAME = PREFIX + "EXTRAS_CONTACT_NAME";
    private static final String EXTRAS_MESSAGE_TYPE = PREFIX + "EXTRAS_MESSAGE_TYPE";
    private static final String EXTRAS_MESSAGE_ID = PREFIX + "EXTRAS_MESSAGE_ID";
    private static final String EXTRAS_EMAIL_GATEWAY = PREFIX + "EXTRAS_EMAIL_GATEWAY";
    // Public EXTRAS strings
    public static final String EXTRAS_NOTIFY = PREFIX + "EXTRAS_NOTIFY";
    public static final String EXTRAS_REMINDER_COUNT = PREFIX + "EXTRAS_REMINDER_COUNT";
    public static final String EXTRAS_REPLYING = PREFIX + "EXTRAS_REPLYING";
    public static final String EXTRAS_QUICKREPLY = PREFIX + "EXTRAS_QUICKREPLY";
    // Sprint vars to check for special voicemail messages
    private static final String SPRINT_BRAND = "sprint";
    private static final String SPRINT_VOICEMAIL_PREFIX = "//ANDROID:";
    // Main message object private vars
    private Context context;
    private String fromAddress = null;
    private String messageBody = null;
    private long timestamp = 0;
    private int unreadCount = 0;
    private long threadId = 0;
    private String contactId = null;
    private String contactLookupKey = null;
    private String contactName = null;
    private int messageType = 0;
    private boolean notify = true;
    private int reminderCount = 0;
    private long messageId = 0;
    private boolean fromEmailGateway = false;
    private MessageClass messageClass = null;
    private String replyText = "";

    /**
     * Construct SmsMmsMessage given a raw message (created from pdu), used for
     * when a message is initially received via the network.
     */
    public SmsMmsMessage(Context _context, SmsMessage[] messages, long _timestamp) {
        SmsMessage sms = messages[0];

        context = _context;
        timestamp = _timestamp;
        messageType = MESSAGE_TYPE_SMS;

        /*
         * Fetch data from raw SMS
         */
        fromAddress = sms.getDisplayOriginatingAddress();
        fromEmailGateway = sms.isEmail();
        messageClass = sms.getMessageClass();

        String body = "";

        try {
            if (messages.length == 1 || sms.isReplace()) {
                body = sms.getDisplayMessageBody();
            } else {
                StringBuilder bodyText = new StringBuilder();
                for (int i = 0; i < messages.length; i++) {
                    bodyText.append(messages[i].getMessageBody());
                }
                body = bodyText.toString();
            }
        } catch (Exception e) {
        }
        messageBody = body;

        /*
         * Lookup the rest of the info from the system db
         */

        SMSUtils.ContactIdentification contactIdentify = null;

        // If this SMS is from an email gateway then lookup contactId by email
        // address
        if (fromEmailGateway) {
            contactIdentify = SMSUtils.getPersonIdFromEmail(context, fromAddress);
            contactName = fromAddress;
        } else { // Else lookup contactId by phone number
            contactIdentify = SMSUtils.getPersonIdFromPhoneNumber(context, fromAddress);
            contactName = PhoneNumberUtils.formatNumber(fromAddress);
        }

        if (contactIdentify != null) {
            contactId = contactIdentify.contactId;
            contactLookupKey = contactIdentify.contactLookup;
            contactName = contactIdentify.contactName;
        }

        unreadCount = SMSUtils.getUnreadMessagesCount(context, timestamp, messageBody);
    }

    /**
     * Construct SmsMmsMessage for getMmsDetails() - fetched from the MMS
     * database table
     */
    public SmsMmsMessage(Context _context, long _messageId, long _threadId, long _timestamp,
                         String _messageBody, int _unreadCount, int _messageType) {

        context = _context;
        messageId = _messageId;
        threadId = _threadId;
        timestamp = _timestamp;
        messageBody = _messageBody;
        unreadCount = _unreadCount;
        messageType = _messageType;

        fromAddress = SMSUtils.getMmsAddress(context, messageId);
        fromEmailGateway = false;

        contactName = PhoneNumberUtils.formatNumber(fromAddress);

        // Look up by phone number first
        SMSUtils.ContactIdentification contactIdentify =
                SMSUtils.getPersonIdFromPhoneNumber(context, fromAddress);

        if (contactIdentify == null) {
            // Lookup by email
            contactIdentify = SMSUtils.getPersonIdFromEmail(context, fromAddress);
            if (contactIdentify != null) {
                // If found then set "from email" flag
                fromEmailGateway = true;
            }
        }

        // If a contact was found then set fields
        if (contactIdentify != null) {
            contactId = contactIdentify.contactId;
            contactLookupKey = contactIdentify.contactLookup;
            contactName = contactIdentify.contactName;
        }
    }

    /**
     * Construct SmsMmsMessage for getSmsDetails() - info fetched from the SMS
     * database table
     */
    public SmsMmsMessage(Context _context, String _fromAddress, String _messageBody,
                         long _timestamp, long _threadId, int _unreadCount, long _messageId, int _messageType) {

        context = _context;
        fromAddress = _fromAddress;
        messageBody = _messageBody;
        timestamp = _timestamp;
        messageType = _messageType;

        SMSUtils.ContactIdentification contactIdentify = null;

        if (PhoneNumberUtils.isWellFormedSmsAddress(fromAddress)) {
            contactIdentify = SMSUtils.getPersonIdFromPhoneNumber(context, fromAddress);
            contactName = PhoneNumberUtils.formatNumber(fromAddress);
            fromEmailGateway = false;
        } else {
            contactIdentify = SMSUtils.getPersonIdFromEmail(context, fromAddress);
            contactName = fromAddress;
            fromEmailGateway = true;
        }

        if (contactIdentify != null) {
            contactId = contactIdentify.contactId;
            contactLookupKey = contactIdentify.contactLookup;
            contactName = contactIdentify.contactName;
        }

        unreadCount = _unreadCount;
        threadId = _threadId;
        messageId = _messageId;
    }

    /**
     * Construct SmsMmsMessage from an extras bundle
     */
    public SmsMmsMessage(Context _context, Bundle b) {
        context = _context;
        fromAddress = b.getString(EXTRAS_FROM_ADDRESS);
        messageBody = b.getString(EXTRAS_MESSAGE_BODY);
        timestamp = b.getLong(EXTRAS_TIMESTAMP);
        contactId = b.getString(EXTRAS_CONTACT_ID);
        contactLookupKey = b.getString(EXTRAS_CONTACT_LOOKUP);
        contactName = b.getString(EXTRAS_CONTACT_NAME);
        unreadCount = b.getInt(EXTRAS_UNREAD_COUNT, 1);
        threadId = b.getLong(EXTRAS_THREAD_ID, 0);
        messageType = b.getInt(EXTRAS_MESSAGE_TYPE, MESSAGE_TYPE_SMS);
        notify = b.getBoolean(EXTRAS_NOTIFY, false);
        reminderCount = b.getInt(EXTRAS_REMINDER_COUNT, 0);
        messageId = b.getLong(EXTRAS_MESSAGE_ID, 0);
        fromEmailGateway = b.getBoolean(EXTRAS_EMAIL_GATEWAY, false);
    }

    /**
     * Construct SmsMmsMessage by specifying all data, only used for testing the
     * notification from the preferences screen
     */
    public SmsMmsMessage(Context _context, String _fromAddress, String _messageBody,
                         long _timestamp, String _contactId, String _contactLookup, String _contactName,
                         int _unreadCount, long _threadId, int _messageType) {
        context = _context;
        fromAddress = _fromAddress;
        messageBody = _messageBody;
        timestamp = _timestamp;
        contactId = _contactId;
        contactLookupKey = _contactLookup;
        contactName = _contactName;
        unreadCount = _unreadCount;
        threadId = _threadId;
        messageType = _messageType;
    }

    /**
     * Convert all SmsMmsMessage data to an extras bundle to send via an intent
     */
    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString(EXTRAS_FROM_ADDRESS, fromAddress);
        b.putString(EXTRAS_MESSAGE_BODY, messageBody);
        b.putLong(EXTRAS_TIMESTAMP, timestamp);
        b.putString(EXTRAS_CONTACT_ID, contactId);
        b.putString(EXTRAS_CONTACT_LOOKUP, contactLookupKey);
        b.putString(EXTRAS_CONTACT_NAME, contactName);
        b.putInt(EXTRAS_UNREAD_COUNT, unreadCount);
        b.putLong(EXTRAS_THREAD_ID, threadId);
        b.putInt(EXTRAS_MESSAGE_TYPE, messageType);
        b.putBoolean(EXTRAS_NOTIFY, notify);
        b.putInt(EXTRAS_REMINDER_COUNT, reminderCount);
        b.putLong(EXTRAS_MESSAGE_ID, messageId);
        b.putBoolean(EXTRAS_EMAIL_GATEWAY, fromEmailGateway);
        return b;
    }

    /**
     * Fetch the "reply to" message intent
     *
     * @param replyToThread whether or not to reply using the message threadId
     *                      or using the sender address
     * @return the intent to pass to startActivity()
     */
    public Intent getReplyIntent(boolean replyToThread) {
        if (messageType == MESSAGE_TYPE_MMS) {
            locateThreadId();
            return SMSUtils.getSmsToIntent(context, threadId);
        } else if (messageType == MESSAGE_TYPE_SMS) {
            locateThreadId();
            /*
             * There are two ways to reply to a message, by "viewing" the
             * threadId or by sending a new message to the address. In most
             * cases we should just execute the former, but in some cases its
             * better to send a new message to an address (apps like Google
             * Voice will intercept this intent as they don't seem to look at
             * the threadId).
             */
            if (replyToThread && threadId > 0) {
                return SMSUtils.getSmsToIntent(context, threadId);
            } else {
                return SMSUtils.getSmsToIntent(context, fromAddress);
            }
        }
        return null;
    }

    public Intent getReplyIntent() {
        return getReplyIntent(true);
    }

    public void setThreadRead() {
        locateThreadId();
        SMSUtils.setThreadRead(context, threadId);
    }

    public void setMessageRead() {
        locateMessageId();
        SMSUtils.setMessageRead(context, messageId, messageType);
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int _unreadCount) {
        unreadCount = _unreadCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MessageClass getMessageClass() {
        return messageClass;
    }

    public CharSequence getFormattedTimestamp() {
        CharSequence formattedTime;
        try {
            formattedTime = DateUtils.formatDateTime(context, timestamp, DateUtils.FORMAT_SHOW_TIME);
        } catch (Exception e) {
            formattedTime = "";
        }
        return formattedTime;
    }

    public String getContactName() {
        if (contactName == null) {
            contactName = context.getString(android.R.string.unknownName);
        }
        return contactName;
    }

    public String getMessageBody() {
        if (messageBody == null) {
            messageBody = "";
        }
        return messageBody;
    }

    public int getMessageType() {
        return messageType;
    }

    public boolean isSms() {
        return messageType == MESSAGE_TYPE_SMS;
    }

    public boolean isMms() {
        return messageType == MESSAGE_TYPE_MMS;
    }

    public void setNotify(boolean mNotify) {
        notify = mNotify;
    }

    public boolean shouldNotify() {
        return notify;
    }

    public int getReminderCount() {
        return reminderCount;
    }

    public void updateReminderCount(int count) {
        reminderCount = count;
    }

    public void incrementReminderCount() {
        reminderCount++;
    }

    public void delete() {
        SMSUtils.deleteMessage(context, getMessageId(), threadId, messageType);
    }

    public void locateThreadId() {
        if (threadId == 0) {
            threadId = SMSUtils.findThreadIdFromAddress(context, fromAddress);
        }
    }

    public long getThreadId() {
        locateThreadId();
        return threadId;
    }

    public void locateMessageId() {
        if (messageId == 0) {
            if (threadId == 0) {
                locateThreadId();
            }
            messageId = SMSUtils.findMessageId(
                    context, threadId, timestamp, messageBody, messageType);
        }
    }

    public long getMessageId() {
        locateMessageId();
        return messageId;
    }

    public String getContactId() {
        return contactId;
    }

    public String getContactLookupKey() {
        return contactLookupKey;
    }

    public Uri getContactLookupUri() {
        if (contactId == null) {
            return null;
        }

        return Uri.withAppendedPath(Contacts.CONTENT_URI, contactId);

        // This seems to fail even though the docs say to use it.
        // return Contacts.getLookupUri(Long.valueOf(contactId),
        // contactLookupKey);
    }

    public String getAddress() {
        return fromAddress;
    }

    public boolean isEmail() {
        return fromEmailGateway;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String text) {
        replyText = text;
    }

    /**
     * Sned a reply to this message
     *
     * @return true of the message was sent, false otherwise
     */
    public boolean replyToMessage(String quickReply) {
/*
        // Mark the message we're replying to as read
        setMessageRead();

        // Send new message
        SmsMessageSender sender =
                new SmsMessageSender(context, new String[] {fromAddress}, quickReply, getThreadId());

        return sender.sendMessage();
        */
        return false;
    }

    // Checks if user is on carrier Sprint and message is a special system message
    public boolean isSprintVisualVoicemail() {

        if (!SPRINT_BRAND.equals(Build.BRAND)) {
            return false;
        }

        if (messageBody != null) {
            if (messageBody.trim().startsWith(SPRINT_VOICEMAIL_PREFIX)) {
                return true;
            }
        }

        return false;
    }

}
