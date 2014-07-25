package com.RSen.Commandr.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.RSen.Commandr.BuildConfig;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSUtils {
    // Content URIs for SMS app, these may change in future SDK
    public static final Uri MMS_SMS_CONTENT_URI = Uri.parse("content://mms-sms/");
    public static final Uri THREAD_ID_CONTENT_URI =
            Uri.withAppendedPath(MMS_SMS_CONTENT_URI, "threadID");
    public static final Uri CONVERSATION_CONTENT_URI =
            Uri.withAppendedPath(MMS_SMS_CONTENT_URI, "conversations");
    public static final String SMSTO_URI = "smsto:";
    public static final Uri SMS_CONTENT_URI = Uri.parse("content://sms");
    public static final Uri SMS_INBOX_CONTENT_URI = Uri.withAppendedPath(SMS_CONTENT_URI, "inbox");
    public static final Uri MMS_CONTENT_URI = Uri.parse("content://mms");
    public static final Uri MMS_INBOX_CONTENT_URI = Uri.withAppendedPath(MMS_CONTENT_URI, "inbox");
    public static final String SMSMMS_ID = "_id";
    public static final String SMS_MIME_TYPE = "vnd.android-dir/mms-sms";
    public static final int READ_THREAD = 1;
    public static final int MESSAGE_TYPE_SMS = 1;
    public static final int MESSAGE_TYPE_MMS = 2;
    // The max size of either the width or height of the contact photo
    public static final int CONTACT_PHOTO_MAXSIZE = 1024;
    public static final Uri DONATE_PAYPAL_URI =
            Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=8246419");

    // Bitmap cache
//    private static final int bitmapCacheSize = 5;
//    private static LruCache<Uri, Bitmap> bitmapCache = null;
    public static final Uri DONATE_MARKET_URI =
            Uri.parse("market://details?id=net.everythingandroid.smspopupdonate");
    public static final Pattern NAME_ADDR_EMAIL_PATTERN =
            Pattern.compile("\\s*(\"[^\"]*\"|[^<>\"]+)\\s*<([^<>]+)>\\s*");
    public static final Pattern QUOTED_STRING_PATTERN =
            Pattern.compile("\\s*\"([^\"]*)\"\\s*");
    private static final String UNREAD_CONDITION = "read=0";
    private static final String[] AUTHOR_CONTACT_INFO =
            {"Adam K <smspopup@everythingandroid.net>"};
    private static final String[] AUTHOR_CONTACT_INFO_DONATE =
            {"Adam K <smspopup+donate@everythingandroid.net>"};

    public static boolean isHoneycomb() {
        // Can use static final constants like HONEYCOMB, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean isICS() {
        // Can use static final constants like ICS, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * Looks up a contacts display name by contact id - if not found, the address (phone number)
     * will be formatted and returned instead.
     */
    public static String getPersonName(Context context, String id, String address) {

        // Check for id, if null return the formatting phone number as the name
        if (id == null) {
            if (address != null) {
                return PhoneNumberUtils.formatNumber(address);
            } else {
                return null;
            }
        }

        Cursor cursor = context.getContentResolver().query(
                Uri.withAppendedPath(Contacts.CONTENT_URI, id),
                new String[]{Contacts.DISPLAY_NAME},
                null, null, null);

        if (cursor != null) {
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    String name = cursor.getString(0);
                    return name;
                }
            } finally {
                cursor.close();
            }
        }

        if (address != null) {
            return PhoneNumberUtils.formatNumber(address);
        }

        return null;
    }

    /**
     * Looks up a contacts display name by contact lookup key - if not found,
     * the address (phone number) will be formatted and returned instead.
     *
     * @param context   Context.
     * @param lookupKey Contact lookup key.
     * @return Contact name or null if not found.
     */
    public static ContactIdentification getPersonNameByLookup(Context context, String lookupKey,
                                                              String contactId) {

        // Check for id, if null return the formatting phone number as the name
        if (lookupKey == null) {
            return null;
        }

        Uri.Builder builder = Contacts.CONTENT_LOOKUP_URI.buildUpon();
        builder.appendPath(lookupKey);
        if (contactId != null) {
            builder.appendPath(contactId);
        }
        Uri uri = builder.build();

        Cursor cursor = context.getContentResolver().query(
                uri,
                new String[]{Contacts._ID, Contacts.LOOKUP_KEY, Contacts.DISPLAY_NAME},
                null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    final String newId = cursor.getString(0);
                    final String newLookup = cursor.getString(1);
                    final String contactName = cursor.getString(2);
                    return new ContactIdentification(newId, newLookup, contactName);
                }
            } finally {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * Looks up a contacts id, given their address (phone number in this case). Returns null if not
     * found
     */
    public static ContactIdentification getPersonIdFromPhoneNumber(
            Context context, String address) {

        if (address == null) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address)),
                    new String[]{PhoneLookup._ID, PhoneLookup.DISPLAY_NAME, PhoneLookup.LOOKUP_KEY},
                    null, null, null);
        } catch (IllegalArgumentException e) {
            return null;
        } catch (Exception e) {
            return null;
        }

        if (cursor != null) {
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    String contactId = String.valueOf(cursor.getLong(0));
                    String contactName = cursor.getString(1);
                    String contactLookup = cursor.getString(2);

                    return new ContactIdentification(contactId, contactLookup, contactName);
                }
            } finally {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * Looks up a contacts id, given their email address. Returns null if not found
     */
    public static ContactIdentification getPersonIdFromEmail(Context context, String email) {
        if (email == null)
            return null;

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    Uri.withAppendedPath(
                            Email.CONTENT_LOOKUP_URI,
                            Uri.encode(extractAddrSpec(email))),
                    new String[]{Email.CONTACT_ID, Email.DISPLAY_NAME_PRIMARY, Email.LOOKUP_KEY},
                    null, null, null
            );
        } catch (IllegalArgumentException e) {
            return null;
        } catch (Exception e) {
            return null;
        }

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {

                    String contactId = String.valueOf(cursor.getLong(0));
                    String contactName = cursor.getString(1);
                    String contactLookup = cursor.getString(2);

                    return new ContactIdentification(contactId, contactLookup, contactName);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Looks up a contact photo by contact id, returns a Bitmap array that represents their photo
     * (or null if not found or there was an error.
     * <p/>
     * I do my own scaling and validation of sizes - Android supports any size for contact photos
     * and some apps are adding huge photos to contacts. Doing the scaling myself allows me more
     * control over how things play out in those cases.
     *
     * @param context the contextze the thumbnail can be
     * @return Bitmap of the contacts photo (null if none or an error)
     */
    public static Bitmap getPersonPhoto(Context context, final Uri contactUri,
                                        final int thumbSize) {

        if (contactUri == null) {
            return null;
        }

        // First let's just check the dimensions of the contact photo
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // The height and width are stored in 'options' but the photo itself is not loaded
        loadContactPhoto(context, contactUri, options);

        // Raw height and width of contact photo
        final int height = options.outHeight;
        final int width = options.outWidth;


        // If photo is too large or not found get out
        if (height > CONTACT_PHOTO_MAXSIZE || width > CONTACT_PHOTO_MAXSIZE ||
                width == 0 || height == 0) {
            return null;
        }

        // This time we're going to do it for real
        options.inJustDecodeBounds = false;

        int newHeight = thumbSize;
        int newWidth = thumbSize;

        // If we have an abnormal photo size that's larger than thumbsize then sample it down
        boolean sampleDown = false;

        if (height > thumbSize || width > thumbSize) {
            sampleDown = true;
        }

        // If the dimensions are not the same then calculate new scaled dimenions
        if (height < width) {
            if (sampleDown) {
                options.inSampleSize = Math.round(height / thumbSize);
            }
            newHeight = Math.round(thumbSize * height / width);
        } else {
            if (sampleDown) {
                options.inSampleSize = Math.round(width / thumbSize);
            }
            newWidth = Math.round(thumbSize * width / height);
        }

        // Fetch the real contact photo (sampled down if needed)
        Bitmap contactBitmap = null;
        try {
            contactBitmap = loadContactPhoto(context, contactUri, options);
        } catch (OutOfMemoryError e) {
        }

        // Not found or error, get out
        if (contactBitmap == null)
            return null;

        // Bitmap scaled to new height and width
        return Bitmap.createScaledBitmap(contactBitmap, newWidth, newHeight, true);
    }

    public static Bitmap getPersonPhoto(Context context, Uri contactUri) {
        if (context == null) {
            return null;
        }
        final Resources res = context.getResources();
        // final int thumbSize = (int) res.getDimension(R.dimen.contact_thumbnail_size);
        //final int thumbBorder = (int) res.getDimension(R.dimen.contact_thumbnail_border);
        return null;// return getPersonPhoto(context, contactUri, thumbSize - thumbBorder);
    }

    /**
     * Opens an InputStream for the person's photo and returns the photo as a Bitmap. If the
     * person's photo isn't present returns the placeholderImageResource instead.
     *
     * @param context the Context
     *                the id of the person
     * @param options the decoding options, can be set to null
     */
    @SuppressLint("NewApi")
    public static Bitmap loadContactPhoto(Context context, Uri contactUri,
                                          BitmapFactory.Options options) {

        if (contactUri == null) {
            return null;
        }

        final InputStream stream;
        if (SMSUtils.isICS()) {
            stream = Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    contactUri, true);
        } else {
            stream = Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    contactUri);
        }

        return stream != null ? BitmapFactory.decodeStream(stream, null, options) : null;
    }

    /**
     * Tries to locate the message thread id given the address (phone or email) of the message
     * sender.
     *
     * @param context a context to use
     * @param address phone number or email address of sender
     * @return the thread id (or 0 if there was a problem)
     */
    synchronized public static long findThreadIdFromAddress(Context context, String address) {
        if (address == null)
            return 0;

        String THREAD_RECIPIENT_QUERY = "recipient";

        Uri.Builder uriBuilder = THREAD_ID_CONTENT_URI.buildUpon();
        uriBuilder.appendQueryParameter(THREAD_RECIPIENT_QUERY, address);

        long threadId = 0;

        Cursor cursor = null;
        try {

            cursor = context.getContentResolver().query(
                    uriBuilder.build(),
                    new String[]{Contacts._ID},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                threadId = cursor.getLong(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return threadId;
    }

    /**
     * Marks a specific message as read
     */
    synchronized public static void setMessageRead(
            Context context, long messageId, int messageType) {

        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        /*
        boolean markRead = myPrefs.getBoolean(
                context.getString(R.string.pref_markread_key),
                Defaults.PREFS_MARK_READ);
        if (!markRead) {
            return;
        }

        if (messageId > 0) {
            ContentValues values = new ContentValues(1);
            values.put("read", READ_THREAD);

            Uri messageUri;

            if (SmsMmsMessage.MESSAGE_TYPE_MMS == messageType) {
                // Used to use URI of MMS_CONTENT_URI and it wasn't working, not sure why
                // this is diff to SMS
                messageUri = Uri.withAppendedPath(MMS_INBOX_CONTENT_URI, String.valueOf(messageId));
            } else if (SmsMmsMessage.MESSAGE_TYPE_SMS == messageType) {
                messageUri = Uri.withAppendedPath(SMS_CONTENT_URI, String.valueOf(messageId));
            } else {
                return;
            }

            // Log.v("messageUri for marking message read: " + messageUri.toString());

            ContentResolver cr = context.getContentResolver();
            int result;
            try {
                result = cr.update(messageUri, values, null, null);
            } catch (Exception e) {
                result = 0;
            }
            if (BuildConfig.DEBUG)
                Log.v(String.format("message id = %s marked as read, result = %s", messageId,
                        result));
        }
        */
    }

    /**
     * Marks a specific message thread as read - all messages in the thread will be marked read
     */
    synchronized public static void setThreadRead(Context context, long threadId) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        /*
        boolean markRead = myPrefs.getBoolean(
                context.getString(R.string.pref_markread_key),
                Defaults.PREFS_MARK_READ);

        if (!markRead)
            return;

        if (threadId > 0) {
            ContentValues values = new ContentValues(1);
            values.put("read", READ_THREAD);

            ContentResolver cr = context.getContentResolver();
            int result = 0;
            try {
                result = cr.update(
                        ContentUris.withAppendedId(CONVERSATION_CONTENT_URI, threadId),
                        values, null, null);
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    Log.v("error marking thread read");
            }
            if (BuildConfig.DEBUG)
                Log.v("thread id " + threadId + " marked as read, result = " + result);
        }
        */
    }

    /**
     * Tries to locate the message id (from the system database), given the message thread id, the
     * timestamp of the message and the type of message (sms/mms)
     */
    synchronized public static long findMessageId(Context context, long threadId, long timestamp,
                                                  String body, int messageType) {

        long id = 0;
        String selection = "body = " + DatabaseUtils.sqlEscapeString(body != null ? body : "");
        selection += " and " + UNREAD_CONDITION;
        final String sortOrder = "date DESC";
        final String[] projection = new String[]{"_id", "date", "thread_id", "body"};

        if (threadId > 0) {
            if (SmsMmsMessage.MESSAGE_TYPE_MMS == messageType) {
                // It seems MMS timestamps are stored in a seconds, whereas SMS timestamps are in
                // millis
                selection += " and date = " + (timestamp / 1000);
            }

            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(CONVERSATION_CONTENT_URI, threadId),
                    projection,
                    selection,
                    null,
                    sortOrder);

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    id = cursor.getLong(0);
                    // Log.v("Timestamp = " + cursor.getLong(1));
                }
            } finally {
                cursor.close();
            }
        }

        if (BuildConfig.DEBUG && id == 0) {
        }

        return id;
    }

    /**
     * Tries to delete a message from the system database, given the thread id, the timestamp of the
     * message and the message type (sms/mms).
     */
    public static void deleteMessage(Context context, long messageId,
                                     long threadId, int messageType) {

        if (messageId > 0) {

            // We need to mark this message read first to ensure the entire thread is marked as read
            setMessageRead(context, messageId, messageType);

            // Construct delete message uri
            Uri deleteUri;

            if (SmsMmsMessage.MESSAGE_TYPE_MMS == messageType) {
                deleteUri = Uri.withAppendedPath(MMS_CONTENT_URI, String.valueOf(messageId));
            } else if (SmsMmsMessage.MESSAGE_TYPE_SMS == messageType) {
                deleteUri = Uri.withAppendedPath(SMS_CONTENT_URI, String.valueOf(messageId));
            } else {
                return;
            }

            int count = 0;
            try {
                count = context.getContentResolver().delete(deleteUri, null, null);
            } catch (Exception e) {
            }

            if (count == 1) {
                // TODO: should only set the thread read if there are no more unread messages
                // setThreadRead(context, threadId);
            }
        }
    }

    /**
     * Fetches a list of unread messages from the system database
     *
     * @param context app context
     *                message id to ignore (the one being displayed), setting this to 0 will return all
     *                unread messages
     * @return ArrayList of SmsMmsMessage
     */

    public static ArrayList<SmsMmsMessage> getUnreadMessages(Context context) {
        ArrayList<SmsMmsMessage> messages = null;

        final String[] projection =
                new String[]{"_id", "thread_id", "address", "date", "body"};
        String selection = UNREAD_CONDITION;
        String[] selectionArgs = null;
        final String sortOrder = "date ASC";

        // Create cursor
        Cursor cursor = context.getContentResolver().query(
                SMS_INBOX_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);

        long messageId;
        long threadId;
        String address;
        long timestamp;
        String body;
        SmsMmsMessage message;

        if (cursor != null) {
            try {
                int count = cursor.getCount();
                if (count > 0) {
                    messages = new ArrayList<SmsMmsMessage>(count);
                    while (cursor.moveToNext()) {
                        messageId = cursor.getLong(0);
                        threadId = cursor.getLong(1);
                        address = cursor.getString(2);
                        timestamp = cursor.getLong(3);
                        body = cursor.getString(4);

                        message = new SmsMmsMessage(
                                context, address, body, timestamp, threadId,
                                count, messageId, SmsMmsMessage.MESSAGE_TYPE_SMS);
                        message.setNotify(false);

                        messages.add(message);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return messages;
    }

    /**
     *
     */
    public static Intent getSmsInboxIntent() {
        Intent conversations = new Intent(Intent.ACTION_MAIN);
        // conversations.addCategory(Intent.CATEGORY_DEFAULT);
        conversations.setType(SMS_MIME_TYPE);
        // should I be using FLAG_ACTIVITY_RESET_TASK_IF_NEEDED??
        int flags =
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP;
        conversations.setFlags(flags);

        return conversations;
    }

    /**
     * Get system view sms thread Intent
     *
     * @param context  context
     * @param threadId the message thread id to view
     * @return the intent that can be started with startActivity()
     */
    public static Intent getSmsToIntent(Context context, long threadId) {
        Intent popup = new Intent(Intent.ACTION_VIEW);

        // Should *NOT* be using FLAG_ACTIVITY_MULTIPLE_TASK however something is broken on
        // a few popular devices that received recent Froyo upgrades that means this is required
        // in order to refresh the system compose message UI
        int flags =
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        // Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP;
        // Intent.FLAG_ACTIVITY_MULTIPLE_TASK;

        popup.setFlags(flags);

        if (threadId > 0) {
            // Log.v("^^Found threadId (" + threadId + "), sending to Sms intent");
            popup.setData(Uri.withAppendedPath(THREAD_ID_CONTENT_URI, String.valueOf(threadId)));
        } else {
            return getSmsInboxIntent();
        }
        return popup;
    }

    /**
     *
     */

    /**
     * Get system sms-to Intent (normally "compose message" activity)
     *
     * @param context     context
     * @param phoneNumber the phone number to compose the message to
     * @return the intent that can be started with startActivity()
     */
    public static Intent getSmsToIntent(Context context, String phoneNumber) {

        Intent popup = new Intent(Intent.ACTION_SENDTO);

        // Should *NOT* be using FLAG_ACTIVITY_MULTIPLE_TASK however something is broken on
        // a few popular devices that received recent Froyo upgrades that means this is required
        // in order to refresh the system compose message UI
        int flags =
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        // Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP;
        // Intent.FLAG_ACTIVITY_MULTIPLE_TASK;

        popup.setFlags(flags);

        if (!"".equals(phoneNumber)) {
            // Log.v("^^Found threadId (" + threadId + "), sending to Sms intent");
            popup.setData(Uri.parse(SMSTO_URI + Uri.encode(phoneNumber)));
        } else {
            return getSmsInboxIntent();
        }
        return popup;
    }

    /**
     * Return current unread message count from system db (sms and mms)
     *
     * @param context
     * @return unread sms+mms message count
     */
    public static int getUnreadMessagesCount(Context context) {
        return getUnreadMessagesCount(context, 0, null);
    }

    /**
     * Return current unread message count from system db (sms and mms)
     *
     * @param context
     * @param timestamp only messages before this timestamp will be counted
     * @return unread sms+mms message count
     */
    synchronized public static int getUnreadMessagesCount(Context context, long timestamp,
                                                          String messageBody) {
        return getUnreadSmsCount(context, timestamp, messageBody) + getUnreadMmsCount(context);
    }

    /**
     * Return current unread message count from system db (sms only)
     *
     * @param context
     * @return unread sms message count
     */
    public static int getUnreadSmsCount(Context context) {
        return getUnreadSmsCount(context, 0, null);
    }

    /**
     * Return current unread message count from system db (sms only)
     *
     * @param context
     * @param timestamp only messages before this timestamp will be counted
     * @return unread sms message count
     */
    private static int getUnreadSmsCount(Context context, long timestamp, String messageBody) {


        final String[] projection = new String[]{SMSMMS_ID, "body"};
        final String selection = UNREAD_CONDITION;
        final String[] selectionArgs = null;
        final String sortOrder = "date DESC";

        int count = 0;

        Cursor cursor = context.getContentResolver().query(
                SMS_INBOX_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);

        if (cursor != null) {
            try {
                count = cursor.getCount();

                /*
                 * We need to check if the message received matches the most recent one in the db or
                 * not (to find out if our code ran before the system code or vice-versa)
                 */
                if (messageBody != null && count > 0) {
                    if (cursor.moveToFirst()) {
                        /*
                         * Check the most recent message, if the body does not match then it hasn't
                         * yet been inserted into the system database, therefore we need to add one
                         * to our total count
                         */
                        if (!messageBody.equals(cursor.getString(1))) {

                            count++;
                        }
                    }
                }
            } finally {
                cursor.close();
            }
        }

        /*
         * If count is still 0 and timestamp is set then its likely the system db had not updated
         * when this code ran, therefore let's add 1 so the notify will run correctly.
         */
        if (count == 0 && timestamp > 0) {
            count = 1;
        }


        return count;
    }

    /**
     * Return current unread message count from system db (mms only)
     *
     * @param context
     * @return unread mms message count
     */
    private static int getUnreadMmsCount(Context context) {

        final String selection = UNREAD_CONDITION;
        final String[] projection = new String[]{SMSMMS_ID};

        int count = 0;

        Cursor cursor = context.getContentResolver().query(
                MMS_INBOX_CONTENT_URI,
                projection,
                selection, null, null);

        if (cursor != null) {
            try {
                count = cursor.getCount();
            } finally {
                cursor.close();
            }
        }
        return count;
    }

    /*
   *
   */
    synchronized public static SmsMmsMessage getSmsDetails(Context context,
                                                           long ignoreThreadId, boolean unreadOnly) {

        final String[] projection =
                new String[]{"_id", "thread_id", "address", "date", "body"};
        String selection = unreadOnly ? UNREAD_CONDITION : null;
        String[] selectionArgs = null;
        final String sortOrder = "date DESC";

        int count = 0;

        if (ignoreThreadId > 0) {
            selection = (selection == null) ? "" : selection + " and ";
            selection += "thread_id != ?";
            selectionArgs = new String[]{String.valueOf(ignoreThreadId)};
        }

        Cursor cursor = context.getContentResolver().query(
                SMS_INBOX_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);

        if (cursor != null) {
            try {
                count = cursor.getCount();
                if (count > 0) {
                    cursor.moveToFirst();

                    long messageId = cursor.getLong(0);
                    long threadId = cursor.getLong(1);
                    String address = cursor.getString(2);
                    long timestamp = cursor.getLong(3);
                    String body = cursor.getString(4);

                    if (!unreadOnly) {
                        count = 0;
                    }

                    SmsMmsMessage smsMessage = new SmsMmsMessage(
                            context, address, body, timestamp, threadId,
                            count, messageId, SmsMmsMessage.MESSAGE_TYPE_SMS);

                    return smsMessage;

                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public static SmsMmsMessage getSmsDetails(Context context) {
        return getSmsDetails(context, 0);
    }

    public static SmsMmsMessage getSmsDetails(Context context, boolean unreadOnly) {
        return getSmsDetails(context, 0, unreadOnly);
    }

    public static SmsMmsMessage getSmsDetails(Context context, long ignoreThreadId) {
        return getSmsDetails(context, ignoreThreadId, true);
    }

    /*
   *
   */
    synchronized public static SmsMmsMessage getMmsDetails(Context context, long ignoreThreadId) {

        final String[] projection = new String[]{"_id", "thread_id", "date", "sub", "sub_cs"};
        String selection = UNREAD_CONDITION;
        String[] selectionArgs = null;
        final String sortOrder = "date DESC";
        int count = 0;

        if (ignoreThreadId > 0) {
            selection += " and thread_id != ?";
            selectionArgs = new String[]{String.valueOf(ignoreThreadId)};
        }

        Cursor cursor = context.getContentResolver().query(
                MMS_INBOX_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);

        if (cursor != null) {
            try {
                count = cursor.getCount();
                if (count > 0) {
                    cursor.moveToFirst();
                    long messageId = cursor.getLong(0);
                    long threadId = cursor.getLong(1);
                    long timestamp = cursor.getLong(2) * 1000;
                    String subject = cursor.getString(3);

                    return new SmsMmsMessage(context, messageId, threadId, timestamp,
                            subject, count, SmsMmsMessage.MESSAGE_TYPE_MMS);
                }

            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public static SmsMmsMessage getMmsDetails(Context context) {
        return getMmsDetails(context, 0);
    }

    public static String getMmsAddress(Context context, long messageId) {
        final String[] projection = new String[]{"address", "contact_id", "charset", "type"};
//        final String selection = "type=137"; // "type="+ PduHeaders.FROM,
        final String selection = null;

        Uri.Builder builder = MMS_CONTENT_URI.buildUpon();
        builder.appendPath(String.valueOf(messageId)).appendPath("addr");

        Cursor cursor = context.getContentResolver().query(
                builder.build(),
                projection,
                selection,
                null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    // Apparently contact_id is always empty in this table so we can't get it from
                    // here

                    // Just return the address
                    return cursor.getString(0);
                }
            } finally {
                cursor.close();
            }
        }

        return context.getString(android.R.string.unknownName);
    }

    private static String extractAddrSpec(String address) {
        Matcher match = NAME_ADDR_EMAIL_PATTERN.matcher(address);

        if (match.matches()) {
            return match.group(2);
        }
        return address;
    }

    private static String getEmailDisplayName(String displayString) {
        Matcher match = QUOTED_STRING_PATTERN.matcher(displayString);
        if (match.matches()) {
            return match.group(1);
        }
        return displayString;
    }

    /**
     * Get the display name of an email address. If the address already contains the name, parse and
     * return it. Otherwise, query the contact database. Cache query results for repeated queries.
     */
    public static String getDisplayName(Context context, String email) {
        Matcher match = NAME_ADDR_EMAIL_PATTERN.matcher(email);
        if (match.matches()) {
            // email has display name, return that
            return getEmailDisplayName(match.group(1));
        }

        // otherwise let's check the contacts list for a user with this email
        // Cursor cursor = context.getContentResolver().query(
        // ContactWrapper.getEmailContentUri(),
        // new String[] { Contacts.ContactMethods.NAME },
        // Contacts.ContactMethods.DATA + " = ?",
        // new String[] { email }, null);
        Cursor cursor = context.getContentResolver().query(
                Uri.withAppendedPath(Email.CONTENT_LOOKUP_URI, Uri.encode(email)),
                new String[]{Contacts.DISPLAY_NAME},
                null, null, null);

        if (cursor != null) {
            try {
                // int columnIndex =
                // cursor.getColumnIndexOrThrow(Contacts.ContactMethods.NAME);
                while (cursor.moveToNext()) {
                    // String name = cursor.getString(columnIndex);
                    String name = cursor.getString(0);
                    if (!TextUtils.isEmpty(name)) {
                        return name;
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return email;
    }

    /*
     * Get the most recent unread message, returning in a SmsMmsMessage which is suitable for
     * updating the notification. Optional param is the message object: we can pull out the thread
     * id of this message in the case the user is "replying" to the message and we should ignore all
     * messages in the thread when working out what to display in the notification bar (as these
     * messages will soon be marked read but we can't be sure when the messaging app will actually
     * start).
     */
    public static SmsMmsMessage getRecentMessage(Context context, SmsMmsMessage ignoreMessage) {
        long ignoreThreadId = 0;

        if (ignoreMessage != null) {
            ignoreThreadId = ignoreMessage.getThreadId();
        }

        SmsMmsMessage smsMessage = getSmsDetails(context, ignoreThreadId);
        SmsMmsMessage mmsMessage = getMmsDetails(context, ignoreThreadId);

        if (mmsMessage == null && smsMessage != null) {
            return smsMessage;
        }

        if (mmsMessage != null && smsMessage == null) {
            return mmsMessage;
        }

        if (mmsMessage != null && smsMessage != null) {
            if (mmsMessage.getTimestamp() < smsMessage.getTimestamp()) {
                return mmsMessage;
            }
            return smsMessage;
        }

        return null;
    }

    public static SmsMmsMessage getRecentMessage(Context context) {
        return getRecentMessage(context, null);
    }

    /**
     * intent.
     *
     * @param intent the intent to read from
     * @return an array of SmsMessages for the PDUs
     */
    public static final SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        if (messages == null) {
            return null;
        }
        if (messages.length == 0) {
            return null;
        }

        byte[][] pduObjs = new byte[messages.length][];

        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }

    /**
     * This function will see if the most recent activity was the system messaging app so we can
     * suppress the popup as the user is likely already viewing messages or composing a new message
     */
    public static final boolean inMessagingApp(Context context) {

        /*
         * These appear to be the 2 main intents that mean the user is using the messaging app
         *
         * action "android.intent.action.MAIN" data null class "com.android.mms.ui.ConversationList"
         * package "com.android.mms"
         *
         * action "android.intent.action.VIEW" data "content://mms-sms/threadID/3" class
         * "com.android.mms.ui.ComposeMessageActivity" package "com.android.mms"
         */
/*
        ActivityManager mAM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<RunningTaskInfo> mRunningTaskList = mAM.getRunningTasks(1);
        Iterator<RunningTaskInfo> mIterator = mRunningTaskList.iterator();

        if (mIterator.hasNext()) {
            RunningTaskInfo mRunningTask = mIterator.next();
            if (mRunningTask != null) {
                ComponentName runningTaskComponent = mRunningTask.baseActivity;

                // Log.v("baseActivity = " + mRunningTask.baseActivity.toString());
                // Log.v("topActivity = " + mRunningTask.topActivity.toString());

                if (SmsMessageSender.MESSAGING_PACKAGE_NAME.equals(runningTaskComponent
                        .getPackageName())
                        &&
                        (SmsMessageSender.MESSAGING_CONVO_CLASS_NAME.equals(runningTaskComponent
                                .getClassName()))
                        ||
                        SmsMessageSender.MESSAGING_COMPOSE_CLASS_NAME.equals(runningTaskComponent
                                .getClassName())) {
                    if (BuildConfig.DEBUG)
                        Log.v("User in messaging app - from running task");
                    return true;
                }
            }
        }
*/
        return false;
    }

    /**
     * Enables or disables the main SMS receiver
     */
    public static void enableSmsPopup(Context context, boolean enable) {
        /*
        PackageManager pm = context.getPackageManager();
        ComponentName cn = new ComponentName(context, SmsReceiver.class);

        // Update preference so it reflects in the preference activity
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor settings = myPrefs.edit();
        settings.putBoolean(context.getString(R.string.pref_enabled_key), enable);
        settings.commit();

        if (enable) {
            if (BuildConfig.DEBUG)
                Log.v("SMSPopup receiver is enabled");
            pm.setComponentEnabledSetting(cn,
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                    PackageManager.DONT_KILL_APP);

        } else {
            if (BuildConfig.DEBUG)
                Log.v("SMSPopup receiver is disabled");
            pm.setComponentEnabledSetting(cn,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
        */
    }

    /**
     * Convert from pixels to density independent pixels.
     *
     * @param res    Resources to fetch display metrics from.
     * @param pixels Pixel dimension to convert.
     * @return Density independent pixels.
     */
    public static int pixelsToDip(Resources res, int pixels) {
        final float scale = res.getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }

    /*
     * Class to hold contact lookup info (as of Android 2.0+ we need the id and lookup key)
     */
    public static class ContactIdentification {
        public String contactId = null;
        public String contactLookup = null;
        public String contactName = null;

        public ContactIdentification(String _contactId, String _contactLookup, String _contactName) {
            contactId = _contactId;
            contactLookup = _contactLookup;
            contactName = _contactName;
        }
    }
}