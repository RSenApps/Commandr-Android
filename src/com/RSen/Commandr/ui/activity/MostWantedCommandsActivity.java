package com.RSen.Commandr.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.core.MostWantedCommands;
import com.RSen.Commandr.ui.card.MostWantedCard;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;

public class MostWantedCommandsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.most_wanted);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint

            tintManager.setStatusBarTintColor(Color.parseColor("#4285f4"));
        }
        ArrayList<Card> cards = new ArrayList<Card>();
        for (MostWantedCommand command : MostWantedCommands.getCommands(this)) {
            cards.add(new MostWantedCard(this, command));
        }
        CardGridArrayAdapter mCardArrayAdapter = new CardGridArrayAdapter(this, cards);
        final CardGridView listView = (CardGridView) findViewById(R.id.card_list_view);

        SwingBottomInAnimationAdapter animCardArrayAdapter = new SwingBottomInAnimationAdapter(mCardArrayAdapter);
        animCardArrayAdapter.setAbsListView(listView);
        listView.setExternalAdapter(animCardArrayAdapter, mCardArrayAdapter);
        findViewById(R.id.vote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MostWantedCommandsActivity.this, VotingActivity.class));
            }
        });
    }
}
