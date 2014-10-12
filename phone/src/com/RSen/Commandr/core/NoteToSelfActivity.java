package com.RSen.Commandr.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         NoteToSelfActivity.java
 * @version 1.0
 *          5/28/14
 */
public class NoteToSelfActivity extends Activity {
    /**
     * Called when the activity is created... run the intercepted command
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String interceptedCommand = getIntent().getExtras().getString(Intent.EXTRA_TEXT); //command phrase sent by Gooogle Now
            if (interceptedCommand != null) {
                if (!CommandInterpreter.interpret(this, interceptedCommand, false)) {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                }
            }
        } catch (Exception e) {
        }

        finish();
    }


}
