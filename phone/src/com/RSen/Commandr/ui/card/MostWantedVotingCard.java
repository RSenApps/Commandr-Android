package com.RSen.Commandr.ui.card;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.RSen.Commandr.R;
import com.parse.ParseObject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/27/2014.
 */
public class MostWantedVotingCard extends Card {
    ParseObject command;

    public MostWantedVotingCard(Context context, ParseObject command) {
        super(context, R.layout.voting_card);
        this.command = command;
        CardHeader header = new CardHeader(context);

        header.setTitle(command.getString("title"));
        addCardHeader(header);
    }


    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView detailTV = (TextView) view.findViewById(R.id.detail);
        final Button voteButton = (Button) view.findViewById(R.id.vote);
        if (detailTV != null) {
            detailTV.setText(command.getString("detail"));
        }
        if (voteButton != null) {
            final int votes = command.getInt("votes");
            voteButton.setText(mContext.getString(R.string.vote) + " (" + votes + ")");
            voteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    voteButton.setText(mContext.getString(R.string.vote) + " (" + (votes + 1) + ")");
                    voteButton.setEnabled(false);
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("voted" + command.getString("title"), true).commit();
                    command.increment("votes");
                    command.saveInBackground();
                }
            });
            voteButton.setEnabled(!PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("voted" + command.getString("title"), false));
        }
    }
}
