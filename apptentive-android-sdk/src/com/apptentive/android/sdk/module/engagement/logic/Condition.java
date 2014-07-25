/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.logic;

/**
 * @author Sky Kelsey
 */
public class Condition {

    public Predicate.Operation operation;
    public Object operand;

    public Condition(Predicate.Operation operation, Object operand) {
        this.operation = operation;
        this.operand = operand;
    }

}
