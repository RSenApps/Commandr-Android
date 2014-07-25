/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;

import com.apptentive.android.sdk.ApptentiveInternal;
import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.model.Configuration;
import com.apptentive.android.sdk.module.engagement.interaction.model.AppStoreRatingInteraction;
import com.apptentive.android.sdk.module.rating.IRatingProvider;
import com.apptentive.android.sdk.module.rating.InsufficientRatingArgumentsException;

import java.util.HashMap;
import java.util.Map;

/**
 * This interaction doesn't display its own UI. It just forwards on the the app store. All app store logic is contained here.
 *
 * @author Sky Kelsey
 */
public class AppStoreRatingInteractionView extends InteractionView<AppStoreRatingInteraction> {

    public AppStoreRatingInteractionView(AppStoreRatingInteraction interaction) {
        super(interaction);

    }

    @Override
    public void show(Activity activity) {
        super.show(activity);

        // TODO: See if we can determine which app store the app was downloaded and go directly there.

        String errorMessage = activity.getString(R.string.apptentive_rating_error);
        try {
            IRatingProvider ratingProvider = ApptentiveInternal.getRatingProvider();
            errorMessage = ratingProvider.activityNotFoundMessage(activity);

            String appDisplayName = Configuration.load(activity).getAppDisplayName();
            Map<String, String> ratingProviderArgs = ApptentiveInternal.getRatingProviderArgs();
            Map<String, String> finalRatingProviderArgs;
            if (ratingProviderArgs != null) {
                finalRatingProviderArgs = new HashMap<String, String>(ratingProviderArgs);
            } else {
                finalRatingProviderArgs = new HashMap<String, String>();
            }

            if (!finalRatingProviderArgs.containsKey("package")) {
                finalRatingProviderArgs.put("package", activity.getPackageName());
            }
            if (!finalRatingProviderArgs.containsKey("name")) {
                finalRatingProviderArgs.put("name", appDisplayName);
            }

            // Engage, then start the rating.
            //Apptentive.engageInternal(activity, interaction.getType().name(), CODE_POINT_RATE);
            ratingProvider.startRating(activity, finalRatingProviderArgs);
        } catch (ActivityNotFoundException e) {
            displayError(activity, errorMessage);
        } catch (InsufficientRatingArgumentsException e) {
            // TODO: Log a message to apptentive to let the developer know that their custom rating provider puked?
            displayError(activity, activity.getString(R.string.apptentive_rating_error));
        } finally {
            activity.finish();
        }
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onBackPressed(Activity activity) {
    }

    private void displayError(Activity activity, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(activity.getString(R.string.apptentive_oops));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.getString(R.string.apptentive_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
