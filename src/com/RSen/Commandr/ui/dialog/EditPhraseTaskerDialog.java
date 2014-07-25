package com.RSen.Commandr.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.TaskerCommand;
import com.RSen.Commandr.core.TaskerCommands;
import com.RSen.Commandr.ui.card.TaskerCard;
import com.RSen.Commandr.util.QustomDialogBuilder;

/**
 * Created by Ryan on 6/11/2014.
 */
public class EditPhraseTaskerDialog {
    public EditPhraseTaskerDialog(final Context context, final TaskerCommand command, final TaskerCard card) {
        QustomDialogBuilder builder = new QustomDialogBuilder(context);
        builder.setTitle(command.taskerCommandName);
        builder.setTitleColor("#0099CC");
        builder.setDividerColor("#0099CC");
        final EditText input = new EditText(context);
        input.setText(command.activationName);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setCustomView(input, context);
        builder.setNegativeButton(context.getString(R.string.cancel), null);
        builder.setPositiveButton(context.getString(R.string.set), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                command.activationName = input.getText().toString();
                TaskerCommands.save(context);
                card.refreshCard(command);
            }
        });
        builder.show();
    }

}
