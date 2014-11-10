package com.RSen.Commandr.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.TaskerCommand;
import com.RSen.Commandr.core.TaskerCommands;
import com.RSen.Commandr.ui.card.TaskerCard;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardGridView;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         TaskerActivity.java
 * @version 1.0
 *          5/28/14
 */
public class TaskerActivity extends ActionBarActivity {
    public SwingBottomInAnimationAdapter animCardArrayAdapter;

    /**
     * Called when the activity is created. Setup the data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasker);

        // Show the Up button in the action bar.
        setupActionBar();
        ArrayList<Card> cards = new ArrayList<Card>();

        for (TaskerCommand command : TaskerCommands.getTaskerCommands(this)) {
            cards.add(new TaskerCard(this, command));
        }
        if (cards.size() == 0) {

            Card card = new Card(this, R.layout.no_tasker_commands_inner);
            CardHeader header = new CardHeader(this);
            header.setTitle("No Tasker Commands");
            card.addCardHeader(header);
            cards.add(card);
        }
        CardGridArrayAdapter mCardArrayAdapter = new CardGridArrayAdapter(this, cards);
        final CardGridView listView = (CardGridView) findViewById(R.id.card_list_view);

        animCardArrayAdapter = new SwingBottomInAnimationAdapter(mCardArrayAdapter);
        animCardArrayAdapter.setAbsListView(listView);
        listView.setExternalAdapter(animCardArrayAdapter, mCardArrayAdapter);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

}
