package com.RSen.Commandr.ui.card;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.TaskerCommand;
import com.RSen.Commandr.ui.activity.TaskerActivity;
import com.RSen.Commandr.ui.dialog.EditPhraseTaskerDialog;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/27/2014.
 */
public class TaskerCard extends Card {
    TaskerCommand command;

    public TaskerCard(Context context, TaskerCommand command) {
        super(context, R.layout.settings_card_row);
        this.command = command;
    }

    @Override
    public CardHeader getCardHeader() {
        return new SwitchHeaderTasker(getContext(), command);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView phraseTV = (TextView) view.findViewById(R.id.normalText);
        ImageButton button = (ImageButton) view.findViewById(R.id.button);

        if (phraseTV != null) {
            phraseTV.setText(command.activationName);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new EditPhraseTaskerDialog(getContext(), command, TaskerCard.this);
                }
            });

        }
    }

    public void refreshCard(TaskerCommand command) {
        try {
            this.command = command;
            getCardView().refreshCard(this);
            ((TaskerActivity) getContext()).animCardArrayAdapter.notifyDataSetChanged();
        } catch (Exception e) {
        }
    }
}
