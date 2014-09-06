package com.RSen.Commandr.ui.card;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.ui.dialog.EditPhraseDialog;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/27/2014.
 */
public class MostWantedCard extends Card {
    MostWantedCommand command;

    public MostWantedCard(Context context, MostWantedCommand command) {
        super(context, R.layout.settings_card_row);
        this.command = command;
    }

    @Override
    public CardHeader getCardHeader() {
        return new SwitchHeader(getContext(), command);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView phraseTV = (TextView) view.findViewById(R.id.normalText);
        ImageButton button = (ImageButton) view.findViewById(R.id.button);

        if (phraseTV != null) {
            if (command.isAvailable(view.getContext())) {
                view.findViewById(R.id.italicized).setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                String phrase = command.getPhrase(view.getContext());
                if (command.getPredicateHint() != null) {
                    phrase = "";
                    String[] phraseParts = command.getPhrase(view.getContext()).split(", ");
                    for (String phrasePart : phraseParts) {
                        phrase += phrasePart + " <" + command.getPredicateHint() + ">, ";
                    }
                    phrase = phrase.substring(0, phrase.length() - 2);
                }
                phraseTV.setText(phrase);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new EditPhraseDialog(getContext(), command, MostWantedCard.this);
                    }
                });
            } else {
                view.findViewById(R.id.italicized).setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                phraseTV.setText(mContext.getString(R.string.not_available_for_device));
            }
        }
    }

    public void refreshCard() {
        try {
            getCardView().refreshCard(this);
        } catch (Exception e) {
        }
    }
}
