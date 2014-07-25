/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.storage;

import com.apptentive.android.sdk.model.StoredFile;

/**
 * @author Sky Kelsey
 */
public interface FileStore {
    public boolean putStoredFile(StoredFile file);

    public StoredFile getStoredFile(String id);

    public void deleteStoredFile(String id);
}
