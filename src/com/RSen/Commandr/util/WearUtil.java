package com.RSen.Commandr.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.RSen.Commandr.core.MostWantedCommands;
import com.RSen.Commandr.core.TaskerCommands;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Ryan on 9/1/2014.
 */
public class WearUtil {
    static GoogleApiClient mGoogleApiClient;
    public static void updateCommandList(final Context context)
    {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        PutDataMapRequest dataMap = PutDataMapRequest.create("/commands");
                        dataMap.getDataMap().putStringArrayList("MOSTWANTEDCOMMANDS", MostWantedCommands.getCommandPhrasesList(context));
                        dataMap.getDataMap().putStringArrayList("TASKERCOMMANDS", TaskerCommands.getCommandPhrasesList(context));
                        PutDataRequest request = dataMap.asPutDataRequest();
                        Wearable.DataApi.putDataItem(mGoogleApiClient, request);
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }
}
