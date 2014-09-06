package com.RSen.Commandr.ui.card;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.TaskerCommand;
import com.RSen.Commandr.core.TaskerCommands;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class SwitchHeaderTasker extends CardHeader {
    TaskerCommand command;

    public SwitchHeaderTasker(Context context, TaskerCommand command) {

        super(context, R.layout.switch_header);
        this.command = command;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        if (view != null) {
            ((TextView) view.findViewById(R.id.title)).setText(command.taskerCommandName);
            ((Switch) view.findViewById(R.id.toggle)).setOnCheckedChangeListener(null);
            ((Switch) view.findViewById(R.id.toggle)).setChecked(command.isEnabled(view.getContext()));
            ((Switch) view.findViewById(R.id.toggle)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    command.isEnabled = b;
                    TaskerCommands.save(compoundButton.getContext());
                }
            });
        }
    }


}
