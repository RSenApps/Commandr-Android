/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.view.survey;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.module.rating.view.ApptentiveBaseDialog;

/**
 * @author Sky Kelsey
 */
public class SurveyThankYouDialog extends ApptentiveBaseDialog {

    public SurveyThankYouDialog(Context context) {
        super(context, R.layout.apptentive_survey_thank_you_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setMessage(String message) {
        final TextView body = (TextView) findViewById(R.id.body);
        body.setText(message);
    }
}
