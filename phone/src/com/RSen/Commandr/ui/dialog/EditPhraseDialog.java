package com.RSen.Commandr.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.ui.card.MostWantedCard;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

/**
 * Created by Ryan on 6/11/2014.
 */
public class EditPhraseDialog {
    public EditPhraseDialog(final Context context, final MostWantedCommand command, final MostWantedCard card) {


        final EditText input = new EditText(context);
        input.setText(command.getPhrase(context));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        Toast.makeText(context, context.getString(R.string.phrase_toast), Toast.LENGTH_LONG).show();
        new MaterialDialog.Builder((Activity) context)
                .title(command.getTitle())
                .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                .customView(input)
                .positiveText(R.string.set)  // the default is 'OK'
                .callback(new MaterialDialog.SimpleCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        command.setPhrase(context, input.getText().toString());
                        card.refreshCard();
                    }
                })
                .negativeText(R.string.cancel)  // leaving this line out will remove the negative button
                .build()
                .show();
    }

}
