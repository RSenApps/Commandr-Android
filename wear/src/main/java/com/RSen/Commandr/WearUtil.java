package com.RSen.Commandr;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.wearable.activity.ConfirmationActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Ryan on 9/1/2014.
 */
public class WearUtil {
    static GoogleApiClient  mGoogleApiClient;
    public static void sendCommandMessage(final Activity context, final String command)
    {

        GoogleApiClient.ConnectionCallbacks sendCommandAction = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle connectionHint) {
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        for (Node node : getConnectedNodesResult.getNodes())
                        {
                           Wearable.MessageApi.sendMessage(
                                    mGoogleApiClient, node.getId(), "/command", command.getBytes()).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                               @Override
                               public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                   Intent intent = new Intent(context, ConfirmationActivity.class);
                                   if (!sendMessageResult.getStatus().isSuccess()) {
                                       intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
                                   }
                                   else {
                                       intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                                   }
                                   intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, command);
                                   context.startActivity(intent);
                               }
                           });
                        }
                    }
                });


            }
            @Override
            public void onConnectionSuspended(int cause) {

            }
        };
        performActionWhenConnected(context, sendCommandAction);
    }
    public static void performActionWhenConnected (final Activity context, GoogleApiClient.ConnectionCallbacks action)
    {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
        {
            action.onConnected(null);
        }
        else {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(action)
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Intent intent = new Intent(context, ConfirmationActivity.class);
                            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);

                            context.startActivity(intent);
                        }
                    })
                    .addApi(Wearable.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    public static void updateListView(final WearActivity context)
    {
        GoogleApiClient.ConnectionCallbacks sendCommandAction = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle connectionHint) {
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        Uri uri = new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).authority(getConnectedNodesResult.getNodes().get(0).getId()).path("/commands").build();
                        Wearable.DataApi.getDataItem(mGoogleApiClient, uri).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                            @Override
                            public void onResult(final DataApi.DataItemResult dataItemResult) {


                                DataMap dataMap = DataMapItem.fromDataItem(dataItemResult.getDataItem()).getDataMap();
                                ArrayList<String> returnList = dataMap.getStringArrayList("MOSTWANTEDCOMMANDS");
                                returnList.addAll(dataMap.getStringArrayList("TASKERCOMMANDS"));
                                context.setupListView(returnList);

                            }
                        });
                    }
                });
            }
            @Override
            public void onConnectionSuspended(int cause) {

            }
        };
        performActionWhenConnected(context, sendCommandAction);

    }

}
