package com.RSen.Commandr.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.core.MostWantedCommands;
import com.RSen.Commandr.ui.card.MostWantedCard;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;

public class MostWantedCommandsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.most_wanted);
        setupActionBar();
        AdView adView = (AdView) this.findViewById(R.id.adView);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("ads", true)) {
            adView.setVisibility(View.VISIBLE);
            Bundle bundle = new Bundle();
            bundle.putString("color_bg", "4285f4");
            bundle.putString("color_bg_top", "4285f4");
            bundle.putString("color_border", "4285f4");
            bundle.putString("color_link", "EEEEEE");
            bundle.putString("color_text", "FFFFFF");
            bundle.putString("color_url", "EEEEEE");
            AdMobExtras extras = new AdMobExtras(bundle);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("E9439BFF2245E1BC1DD0FDB28EA467F9").addTestDevice("49924C4BF3738C69A7497A524D092901").addNetworkExtras(extras).build();
            adView.loadAd(adRequest);
        } else {
            adView.setVisibility(View.GONE);
        }

        ArrayList<Card> cards = new ArrayList<Card>();
        for (MostWantedCommand command : MostWantedCommands.getCommands(this)) {
            cards.add(new MostWantedCard(this, command));
        }
        CardGridArrayAdapter mCardArrayAdapter = new CardGridArrayAdapter(this, cards);
        final CardGridView listView = (CardGridView) findViewById(R.id.card_list_view);
        ((FloatingActionButton)findViewById(R.id.vote)).attachToListView(listView);
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
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
