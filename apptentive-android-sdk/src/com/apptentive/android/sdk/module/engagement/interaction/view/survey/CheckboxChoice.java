/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.view.survey;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.apptentive.android.sdk.R;

/**
 * @author Sky Kelsey.
 */
public class CheckboxChoice extends FrameLayout {

    protected CheckBox checkbox;

    public CheckboxChoice(Context context, String textString) {
        super(context);

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View choice = inflater.inflate(R.layout.apptentive_survey_question_multichoice_choice, this);

        TextView text = (TextView) choice.findViewById(R.id.choice_text);
        checkbox = (CheckBox) choice.findViewById(R.id.checkbox);

        text.setText(textString);
        setClickable(true);
        checkbox.setClickable(false);
    }

    public void toggle() {
        checkbox.toggle();
    }

    public void check() {
        checkbox.setChecked(true);
    }

    public boolean isChecked() {
        return checkbox.isChecked();
    }
}
