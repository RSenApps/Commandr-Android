/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.view;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.module.engagement.EngagementModule;
import com.apptentive.android.sdk.module.engagement.interaction.model.UpgradeMessageInteraction;

/**
 * @author Sky Kelsey
 */
public class UpgradeMessageInteractionView extends InteractionView<UpgradeMessageInteraction> {

    private static final String CODE_POINT_LAUNCH = "launch";
    private static final String CODE_POINT_DISMISS = "dismiss";

    public UpgradeMessageInteractionView(UpgradeMessageInteraction interaction) {
        super(interaction);
    }

    @Override
    public void show(Activity activity) {
        super.show(activity);
        activity.setContentView(R.layout.apptentive_upgrade_message_interaction);

        EngagementModule.engageInternal(activity, interaction.getType().name(), CODE_POINT_LAUNCH);

        ImageView iconView = (ImageView) activity.findViewById(R.id.icon);
        Drawable icon = getIconDrawableResourceId(activity);
        if (icon != null) {
            iconView.setImageDrawable(icon);
        } else {
            iconView.setVisibility(View.GONE);
        }
        WebView webview = (WebView) activity.findViewById(R.id.webview);
        webview.loadData(interaction.getBody(), "text/html", "UTF-8");
        webview.setBackgroundColor(Color.TRANSPARENT); // Hack to keep webview background from being colored after load.

        // If branding is not desired, turn the view off.
        if (!interaction.isShowPoweredBy()) {
            activity.findViewById(R.id.apptentive_branding_view).setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onBackPressed(Activity activity) {
        EngagementModule.engageInternal(activity, interaction.getType().name(), CODE_POINT_DISMISS);
    }

    private Drawable getIconDrawableResourceId(Activity activity) {
        try {
            PackageManager pm = activity.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), 0);
            return activity.getResources().getDrawable(pi.applicationInfo.icon);
        } catch (Exception e) {
            Log.e("Error loading app icon.", e);
        }
        return null;
    }
}
