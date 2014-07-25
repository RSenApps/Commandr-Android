package com.RSen.Commandr.ui.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.ui.card.MostWantedVotingCard;
import com.RSen.Commandr.util.QustomDialogBuilder;
import com.apptentive.android.sdk.ApptentiveActivity;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;

public class VotingActivity extends ApptentiveActivity {
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voting);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint

            tintManager.setStatusBarTintColor(Color.parseColor("#4285f4"));
        }
        Parse.initialize(this, "giwroIjJIvqQCpTB23LsdDYe8HdHCxZwy0fkKksV", "H98VqurfJTM4J05D8Fmht3VyXdbTCb5ghBf8Jkjc");


    }

    @Override
    protected void onResume() {
        super.onResume();
        progress.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("VotingCommand");
        query.addDescendingOrder("votes");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    ArrayList<Card> cards = new ArrayList<Card>();
                    for (ParseObject command : parseObjects) {

                        cards.add(new MostWantedVotingCard(VotingActivity.this, command));

                    }
                    final CardGridArrayAdapter mCardArrayAdapter = new CardGridArrayAdapter(VotingActivity.this, cards);
                    final CardGridView listView = (CardGridView) findViewById(R.id.card_list_view);

                    final SwingBottomInAnimationAdapter animCardArrayAdapter = new SwingBottomInAnimationAdapter(mCardArrayAdapter);
                    animCardArrayAdapter.setAbsListView(listView);
                    listView.setExternalAdapter(animCardArrayAdapter, mCardArrayAdapter);
                    progress.setVisibility(View.GONE);
                    Button b = (Button) findViewById(R.id.vote);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            QustomDialogBuilder builder = new QustomDialogBuilder(VotingActivity.this);
                            builder.setTitle("Suggest New Command");
                            builder.setTitleColor("#0099CC");
                            builder.setDividerColor("#0099CC");
                            final View v = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.suggest_command, null);
                            builder.setCustomView(v, VotingActivity.this);
                            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String name = ((EditText) v.findViewById(R.id.name)).getText().toString();
                                    String detail = ((EditText) v.findViewById(R.id.detailSuggest)).getText().toString();
                                    ParseObject votingCommand = new ParseObject("VotingCommand");
                                    votingCommand.put("title", name);
                                    votingCommand.put("detail", detail);
                                    votingCommand.put("votes", 0);
                                    votingCommand.saveInBackground();
                                    mCardArrayAdapter.add(new MostWantedVotingCard(VotingActivity.this, votingCommand));
                                    animCardArrayAdapter.notifyDataSetChanged();
                                    Toast.makeText(VotingActivity.this, getString(R.string.suggestion_submitted), Toast.LENGTH_LONG).show();
                                }
                            });
                            builder.setNegativeButton("Cancel", null);
                            builder.show();
                        }
                    });

                }
            }
        });

    }


}
