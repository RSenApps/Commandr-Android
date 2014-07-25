package com.RSen.Commandr.builtincommands;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.util.HashMap;

public class PlayPlaylistActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String playlistName = getIntent().getStringExtra("playlistname");
        HashMap<String, Integer> playlists = getPlaylistMap();

        if (playlists == null || playlists.size() == 0) {
            Toast.makeText(this, "No Playlists Found.", Toast.LENGTH_LONG).show();
        } else {

            if (playlists.containsKey(playlistName)) {
                playPlaylist(playlists.get(playlistName));//1-based last-added is always first

            } else {
                Toast.makeText(this, "Playlist not found", Toast.LENGTH_LONG).show();
            }
        }

        finish();
    }

    public HashMap<String, Integer> getPlaylistMap() {

        // In the next line 'this' points to current Activity.
        // If you want to use the same code in other java file then activity,
        // then use an instance of any activity in place of 'this'.

        Cursor playListCursor = getContentResolver().query(
                Uri.parse("content://com.google.android.music.MusicContent/playlists"),
                new String[]{"playlist_id", "playlist_name"},
                null, null, null);

        if (playListCursor == null) {
            //System.out.println("Not having any Playlist on phone --------------");
            return null;//don't have list on phone
        }

        System.gc();

        String playListName = null;
        int playListId;
        //System.out.println(">>>>>>>  CREATING AND DISPLAYING LIST OF ALL CREATED PLAYLIST  <<<<<<");
        HashMap<String, Integer> returnMap = new HashMap<String, Integer>();
        for (int i = 0; i < playListCursor.getCount(); i++) {
            playListCursor.moveToPosition(i);
            playListName = playListCursor.getString(playListCursor.getColumnIndex("playlist_name"));
            playListId = playListCursor.getInt(playListCursor.getColumnIndex("playlist_id"));
            returnMap.put(playListName.toLowerCase().trim(), playListId);
        }

        if (playListCursor != null)
            playListCursor.close();
        return returnMap;
    }

    //which is 1-based
    private void playPlaylist(int which) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setType("vnd.android.cursor.dir/vnd.google.music.playlist");
        i.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP | Intent.FLAG_RECEIVER_FOREGROUND | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("playlist", which + "");
        startActivity(i);
        Toast.makeText(this, "Unfortunately, Google Play Music only allows opening the playlist instead of playing a playlist directly. Working on a way arouond this...", Toast.LENGTH_LONG).show();
    }

}
