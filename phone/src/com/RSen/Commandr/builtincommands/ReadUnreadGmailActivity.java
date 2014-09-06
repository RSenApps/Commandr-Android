package com.RSen.Commandr.builtincommands;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MyAccessibilityService;
import com.RSen.Commandr.util.GoogleNowUtil;
import com.RSen.Commandr.util.TTSService;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightActivity.java
 * @version 1.0
 *          5/28/14
 */
public class ReadUnreadGmailActivity extends Activity {
    private static final String GMAIL_SCOPE = "oauth2:https://www.googleapis.com/auth/gmail.readonly";
    private static final String APP_NAME = "Commandr";
    String accountName;
    Gmail mailService;
    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Shows Account Picker with google accounts if not stored in shared
        // preferences
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean accountFound = false;
        if (sharedPrefs.contains("gmailId")) {
            Account[] accounts = AccountManager.get(this)
                    .getAccounts();
            accountName = sharedPrefs.getString("gmailId", null);
            for (Account a : accounts) {
                if (a.type.equals(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)
                        && a.name.equals(accountName)) {
                    accountFound = true;
                    new getAuthToken().execute();
                    break;
                }
            }
        }

        if (!accountFound) {
            Intent googlePicker = AccountPicker.newChooseAccountIntent(null,
                    null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},
                    true, null, null, null, null);
            startActivityForResult(googlePicker, 1);
        }

    }

    // Gets selected email account and runs getAuthToken AsyncTask for selected
    // account
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Editor editor = sharedPrefs.edit();
            editor.putString("gmailId", accountName);
            editor.apply();
            new getAuthToken().execute();
        }
    }

    // Gets oauth2 token using Play Services SDK and runs connectIMAP task after
    // receiving token
    public class getAuthToken extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                String token = GoogleAuthUtil.getToken(ReadUnreadGmailActivity.this,
                        accountName, GMAIL_SCOPE);
                GoogleCredential credential = new GoogleCredential()
                        .setAccessToken(token);
                HttpTransport httpTransport = new NetHttpTransport();
                JsonFactory jsonFactory = new JacksonFactory();

                mailService = new Gmail.Builder(httpTransport, jsonFactory,
                        credential).setApplicationName(APP_NAME).build();
                // Retrieve a page of Threads; max of 100 by default.
                ListMessagesResponse response = mailService.users().messages().list("me").setQ("is:unread in:inbox").execute();

                List<Message> messages = new ArrayList<Message>();
                while (response.getMessages() != null) {
                    messages.addAll(response.getMessages());
                    if (response.getNextPageToken() != null) {
                        String pageToken = response.getNextPageToken();
                        response = mailService.users().messages().list("me").setQ("is:unread in:inbox")
                                .setPageToken(pageToken).execute();
                    } else {
                        break;
                    }
                }
                String toSpeak;
                switch (messages.size()) {
                    case 0:
                        toSpeak = getString(R.string.no_unread) + " ";
                        break;
                    case 1:
                        toSpeak = getString(R.string.one_unread) + " ";
                        break;
                    default:
                        toSpeak = getString(R.string.there_are) + " " + messages.size() + " " + getString(R.string.unread_messages) + " ";
                        break;
                }
                for (Message message : messages) {
                    message = mailService.users().messages().get("me", message.getId()).setFormat("full").setFields("payload, snippet").execute();
                    for (MessagePartHeader header : message.getPayload().getHeaders()) {
                        if (header.getName().equals("From")) {
                            toSpeak += header.getValue().split("<")[0].trim().replace("\\", "").replace("\"", "") + " " + getString(R.string.sent) + " ";
                        }
                    }
                    toSpeak += message.getSnippet() + ". ";
                }
                return toSpeak;
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getString(R.string.error_gmail);
        }

        @Override
        protected void onPostExecute(String result) {
            Intent i = new Intent(ReadUnreadGmailActivity.this, TTSService.class);
            i.putExtra("toSpeak", result);
            startService(i);
            try {
                GoogleNowUtil.resetGoogleNow(MyAccessibilityService.getInstance());
            } catch (Exception e) {
                GoogleNowUtil.resetGoogleNow(ReadUnreadGmailActivity.this);
            }
            finish();
        }

    }

}
