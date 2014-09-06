package com.RSen.Commandr.builtincommands;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.RSen.Commandr.R;

public class RootCommandActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String[] commands = getIntent().getStringArrayExtra("command");
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Process proc = Runtime.getRuntime().exec(commands);
                    proc.waitFor();
                } catch (Exception ex) {
                    (RootCommandActivity.this).runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(RootCommandActivity.this, getString(R.string.root_command_failed), Toast.LENGTH_SHORT).show();
                        }
                    });
                    ex.printStackTrace();
                }
                (RootCommandActivity.this).runOnUiThread(new Runnable() {
                    public void run() {
                        finish();
                    }
                });
            }
        }, "RestartNowRootOnly ").start();

    }

}
