package com.RSen.Commandr.builtincommands;

import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.RSen.Commandr.R;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AudioCaptureActivity extends Activity {

    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private Button start,stop,play;
    private TextView display;
    MediaPlayer m;
    CountDownTimer timer;
    boolean dontFinishTimer = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_capture);
        start = (Button)findViewById(R.id.start);
        stop = (Button)findViewById(R.id.stop);
        play = (Button)findViewById(R.id.play);
        display = (TextView) findViewById(R.id.textView1);
        stop.setEnabled(false);
        play.setEnabled(false);
        m = new MediaPlayer();
        setFinishOnTouchOutside(false);
        File file = new File(Environment.getExternalStorageDirectory().
                getAbsolutePath() + "/CommandrAudio/");
        file.mkdir();
        outputFile = file.getAbsolutePath() + "/" + new SimpleDateFormat("HH.mm.ss MM-dd-yyyy").format(new Date()) +".3gp";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);


    }

    @Override
    protected void onResume() {
        super.onResume();
        int seconds = getIntent().getIntExtra("seconds", -1);
        if (seconds != -1)
        {
            display.setText(getString(R.string.recording) + " - " + seconds + " " + getString(R.string.seconds));
            start(start);
            dontFinishTimer = false;
            timer = new CountDownTimer(seconds * 1000, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

                public void onTick(long millisUntilFinished) {
                    display.setText(getString(R.string.recording) + " - " + millisUntilFinished/1000 + " " + getString(R.string.seconds));
                }

                public void onFinish() {
                    if(dontFinishTimer) {
                        stop(stop);
                    }
                }
            }.start();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop(stop);
        try {
            timer.cancel();
        }
        catch (Exception e)
        {}
        try {
           m.release();
        }
        catch (Exception e)
        {}
        finish();
    }

    public void start(View view){
        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        start.setEnabled(false);
        stop.setEnabled(true);
        display.setText(R.string.recording);

    }

    public void stop(View view) {
        try {
            dontFinishTimer = true;
            timer.cancel();
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder = null;
            stop.setEnabled(false);
            play.setEnabled(true);
            display.setText(R.string.stop);
            Toast.makeText(getApplicationContext(), getString(R.string.audio_recording_successful) + " " + outputFile,
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {}

    }

    public void play(View view) throws IllegalArgumentException,
            SecurityException, IllegalStateException, IOException{


        m.setDataSource(outputFile);
        m.prepare();
        m.start();
        Toast.makeText(getApplicationContext(), getString(R.string.playing_audio), Toast.LENGTH_LONG).show();

    }

}