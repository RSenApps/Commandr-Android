package com.RSen.Commandr.core;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.UnsupportedEncodingException;

/**
 * Created by Ryan on 8/31/2014.
 */
public class WearListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if("/command".equals(messageEvent.getPath())) {
             String command = new String (messageEvent.getData());
            CommandInterpreter.interpret(this, command, false, true);
        }
    }
}