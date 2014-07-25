/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter.view;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.module.rating.view.ApptentiveBaseDialog;
import com.apptentive.android.sdk.util.Util;

/**
 * @author Sky Kelsey
 */
public class MessageCenterIntroDialog extends ApptentiveBaseDialog {

    private OnSendListener onSendListener;
    private boolean emailRequired = false;
    private CharSequence email;
    private CharSequence message;

    public MessageCenterIntroDialog(Context context) {
        super(context, R.layout.apptentive_message_center_intro_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AutoCompleteTextView emailText = (AutoCompleteTextView) findViewById(R.id.email);
        final EditText messageText = (EditText) findViewById(R.id.message);
        final Button noThanksButton = (Button) findViewById(R.id.no_thanks);
        final Button sendButton = (Button) findViewById(R.id.send);

        // Pre-populate a list of possible emails based on those pulled from the phone.
        ArrayAdapter<String> emailAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, Util.getAllUserAccountEmailAddresses(getContext()));
        emailText.setAdapter(emailAdapter);
        emailText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                emailText.showDropDown();
                return false;
            }
        });
        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                email = charSequence;
                validateForm(sendButton);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                message = charSequence;
                validateForm(sendButton);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        noThanksButton.setEnabled(true);
        noThanksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        sendButton.setEnabled(false);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email != null && email.length() != 0 && !Util.isEmailValid(email.toString())) {
                    EmailValidationFailedDialog dialog = new EmailValidationFailedDialog(getContext());
                    dialog.show();
                    return;
                }
                if (MessageCenterIntroDialog.this.onSendListener != null) {
                    onSendListener.onSend(emailText.getText().toString(), messageText.getText().toString());
                }
            }
        });
        validateForm(sendButton);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getResources().getString(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setText(title);
    }

    public void setBody(int bodyId) {
        setBody(getContext().getResources().getString(bodyId));
    }

    public void setBody(CharSequence body) {
        TextView textView = (TextView) findViewById(R.id.body);
        textView.setText(body);
    }

    public void setEmailFieldHidden(boolean hidden) {
        EditText email = (EditText) findViewById(R.id.email);
        if (hidden) {
            email.setVisibility(View.GONE);
        } else {
            email.setVisibility(View.VISIBLE);
        }
    }

    public boolean isEmailFieldVisible() {
        EditText email = (EditText) findViewById(R.id.email);
        return email.getVisibility() == View.VISIBLE;
    }

    public void prePopulateEmail(String email) {
        EditText emailEditText = (EditText) findViewById(R.id.email);
        emailEditText.setText(email);
        this.email = email;
    }

    public void setEmailRequired(boolean emailRequired) {
        this.emailRequired = emailRequired;
        final AutoCompleteTextView emailText = (AutoCompleteTextView) findViewById(R.id.email);
        if (emailRequired) {
            emailText.setHint(R.string.apptentive_edittext_hint_email_required);
        } else {
            emailText.setHint(R.string.apptentive_edittext_hint_email);
        }
    }

    private void validateForm(Button sendButton) {
        boolean passedEmail = true;
        if (emailRequired) {
            passedEmail = !(email == null || email.length() == 0);
        }
        boolean passedMessage = !(message == null || message.length() == 0);

        sendButton.setEnabled(passedEmail && passedMessage);
    }

    public void setOnSendListener(OnSendListener onSendListener) {
        this.onSendListener = onSendListener;
    }

    public interface OnSendListener {
        public void onSend(String email, String message);
    }
}
