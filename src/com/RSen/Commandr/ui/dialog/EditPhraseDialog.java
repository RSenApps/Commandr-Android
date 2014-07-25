package com.RSen.Commandr.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.ui.card.MostWantedCard;
import com.RSen.Commandr.util.QustomDialogBuilder;

/**
 * Created by Ryan on 6/11/2014.
 */
public class EditPhraseDialog {
    public EditPhraseDialog(final Context context, final MostWantedCommand command, final MostWantedCard card) {
        QustomDialogBuilder builder = new QustomDialogBuilder(context);
        builder.setTitle(command.getTitle());
        builder.setTitleColor("#0099CC");
        builder.setDividerColor("#0099CC");
        final EditText input = new EditText(context);
        input.setText(command.getPhrase(context));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setCustomView(input, context);
        builder.setNegativeButton(context.getString(R.string.cancel), null);
        builder.setPositiveButton(context.getString(R.string.set), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                command.setPhrase(context, input.getText().toString());
                card.refreshCard();
            }
        });
        builder.show();
    }

}
