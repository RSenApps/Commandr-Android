/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.model.survey;

/**
 * @author Sky Kelsey.
 */
public interface Question {
    public static final int QUESTION_TYPE_SINGLELINE = 1;
    public static final int QUESTION_TYPE_MULTICHOICE = 2;
    public static final int QUESTION_TYPE_MULTISELECT = 3;

    public int getType();

    public String getId();

    public String getValue();

    public boolean isRequired();

    public String getInstructions();

    public int getMinSelections();

    public int getMaxSelections();

    public enum Type {
        multichoice,
        singleline,
        multiselect,
    }
}
