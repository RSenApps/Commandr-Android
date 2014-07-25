package com.RSen.Commandr.ui.card;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class SwitchHeader extends CardHeader {
    MostWantedCommand command;

    public SwitchHeader(Context context, MostWantedCommand command) {

        super(context, R.layout.switch_header);
        this.command = command;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        if (view != null) {
            ((TextView) view.findViewById(R.id.title)).setText(command.getTitle());
            ((Switch) view.findViewById(R.id.toggle)).setOnCheckedChangeListener(null);
            ((Switch) view.findViewById(R.id.toggle)).setChecked(command.isEnabled(view.getContext()));
            ((Switch) view.findViewById(R.id.toggle)).setEnabled(command.isAvailable(view.getContext()));
            ((Switch) view.findViewById(R.id.toggle)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    PreferenceManager.getDefaultSharedPreferences(compoundButton.getContext()).edit().putBoolean(command.getTitle(), b).commit();
                }
            });
        }
    }


}
