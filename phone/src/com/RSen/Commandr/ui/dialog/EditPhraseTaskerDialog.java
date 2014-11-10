package com.RSen.Commandr.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.TaskerCommand;
import com.RSen.Commandr.core.TaskerCommands;
import com.RSen.Commandr.ui.card.TaskerCard;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

/**
 * Created by Ryan on 6/11/2014.
 */
public class EditPhraseTaskerDialog {
    public EditPhraseTaskerDialog(final Context context, final TaskerCommand command, final TaskerCard card) {
        final EditText input = new EditText(context);
        input.setText(command.activationName);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        new MaterialDialog.Builder((Activity) context)
                .title(command.taskerCommandName)
                .content("Let Google help apps determine location. This means sending anonymous location data to Google, even when no apps are running.")
                .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                .customView(input)
                .positiveText(R.string.set)  // the default is 'OK'
                .callback(new MaterialDialog.SimpleCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {

                        command.activationName = input.getText().toString();
                        TaskerCommands.save(context);
                        card.refreshCard(command);
                    }
                })
                .negativeText(R.string.cancel)  // leaving this line out will remove the negative button
                .build()
                .show();

    }

}
