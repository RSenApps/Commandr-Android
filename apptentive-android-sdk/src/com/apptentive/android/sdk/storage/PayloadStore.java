package com.apptentive.android.sdk.storage;

import com.apptentive.android.sdk.model.Payload;

/**
 * @author Sky Kelsey
 */
public interface PayloadStore {

    public void addPayload(Payload... payloads);

    public void deletePayload(Payload payload);

    public void deleteAllPayloads();

    public Payload getOldestUnsentPayload();

}
