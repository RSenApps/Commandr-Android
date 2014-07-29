/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.RSen.Commandr.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.RSen.Commandr.R;


public final class EditActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BundleScrubber.scrub(getIntent());

        final Bundle localeBundle = getIntent().getBundleExtra(LocaleIntent.EXTRA_BUNDLE);

        BundleScrubber.scrub(localeBundle);

        setContentView(R.layout.tasker_edit_activity);
        final EditText phraseEdit = (EditText) findViewById(R.id.editText);
        if (null == savedInstanceState) {
            if (PluginBundleManager.isBundleValid(localeBundle)) {
                final String savedPhrase =
                        localeBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_PHRASE);
                phraseEdit.setText(savedPhrase);
            }
        }
        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent resultIntent = new Intent();

                /*
                 * This extra is the data to ourselves: either for the Activity or the BroadcastReceiver. Note
                 * that anything placed in this Bundle must be available to Locale's class loader. So storing
                 * String, int, and other standard objects will work just fine. Parcelable objects are not
                 * acceptable, unless they also implement Serializable. Serializable objects must be standard
                 * Android platform objects (A Serializable class private to this plug-in's APK cannot be
                 * stored in the Bundle, as Locale's classloader will not recognize it).
                 */
                final Bundle resultBundle =
                        PluginBundleManager.generateBundle(getApplicationContext(), phraseEdit.getText().toString());
                resultIntent.putExtra(LocaleIntent.EXTRA_BUNDLE, resultBundle);

                /*
                 * The blurb is concise status text to be displayed in the host's UI.
                 */
                resultIntent.putExtra(LocaleIntent.EXTRA_STRING_BLURB,
                        getString(R.string.tasker_edit_activity_blurb) + phraseEdit.getText().toString());

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        setResult(RESULT_CANCELED);
    }
}