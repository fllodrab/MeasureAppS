package com.example.fllodrab.measureappss;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Parameters extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, DiscreteSeekBar.OnProgressChangeListener {
    Map seekBars = new HashMap();
    private static final int TOTAL_AMOUNT = 100; // the maximum amount for all SeekBars
    // stores the current progress for the SeekBars(initially each SeekBar has a
    // progress of 20)
    private int[] mAllProgress = { 0, 0, 0, 0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /** SeekBars escuhando */
        DiscreteSeekBar ramSeekBar = (DiscreteSeekBar) findViewById(R.id.ram);
        ramSeekBar.setOnProgressChangeListener(this);

        DiscreteSeekBar cpuSeekBar = (DiscreteSeekBar) findViewById(R.id.cpu);
        cpuSeekBar.setOnProgressChangeListener(this);

        DiscreteSeekBar sentSeekBar = (DiscreteSeekBar) findViewById(R.id.upload);
        sentSeekBar.setOnProgressChangeListener(this);

        DiscreteSeekBar receivedSeekBar = (DiscreteSeekBar) findViewById(R.id.download);
        receivedSeekBar.setOnProgressChangeListener(this);

        DiscreteSeekBar ratingSeekBar = (DiscreteSeekBar) findViewById(R.id.ratings);
        ratingSeekBar.setOnProgressChangeListener(this);

        DiscreteSeekBar nDownloadsSeekBar = (DiscreteSeekBar) findViewById(R.id.number_downloads);
        nDownloadsSeekBar.setOnProgressChangeListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Elaborando ranking...", Snackbar.LENGTH_LONG)
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                Intent intent = new Intent(Parameters.this, Ranking.class);
                                startActivity(intent);
                            }
                        }).show();
            }
        });
    }

    /**
     * Returns the still available progress after the difference between the
     * maximum value(TOTAL_AMOUNT = 100) and the sum of the store progresses of
     * all SeekBars.
     *
     * @return the available progress.
     */
    private final int remaining() {
        int remaining = TOTAL_AMOUNT;
        for (int i = 0; i < 5; i++) {
            remaining = remaining -  mAllProgress[i];
        }
        if (remaining >= 100) {
            Log.d("REMAINING>100", String.valueOf(remaining));
            remaining = 100;
        } else if (remaining <= 0) {
            Log.d("REMAINING<100", String.valueOf(remaining));
            remaining = 0;
        }
        return remaining;
    }

    private int whichIsIt(int id) {
        switch (id) {
            case R.id.ram:
                return 0; // first position in mAllProgress
            case R.id.cpu:
                return 1;
            case R.id.upload:
                return 2;
            case R.id.download:
                return 3;
            case R.id.ratings:
                return 4;
            case R.id.number_downloads:
                return 5;
            default:
                throw new IllegalStateException(
                        "There should be a Seekbar with this id(" + id + ")!");
        }
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
        // find out which SeekBar triggered the event so we can retrieve its saved current
        // progress
        int which = whichIsIt(seekBar.getId());
        // the stored progress for this SeekBar
        int storedProgress = mAllProgress[which];
        // we basically have two cases, the user either goes to the left or to
        // the right with the thumb. If he goes to the right we must check to
        // see how much he's allowed to go in that direction(based on the other
        // SeekBar values) and stop him if he the available progress was used. If
        // he goes to the left use that progress as going back
        // and freeing the track isn't a problem.
        if (progress > storedProgress) {
            // how much is currently available based on all SeekBar progress
            int remaining = remaining();
            // if there's no progress remaining then simply set the progress at
            // the stored progress(so the user can't move the thumb further)
            if (remaining == 0) {
                seekBar.setProgress(storedProgress);
                return;
            } else {
                // we still have some progress available so check that available
                // progress and let the user move the thumb as long as the
                // progress is at most as the sum between the stored progress
                // and the maximum still available progress
                if (storedProgress + remaining >= progress) {
                    mAllProgress[which] = progress;
                } else {
                    // the current progress is bigger then the available
                    // progress so restrict the value
                    mAllProgress[which] = storedProgress + remaining;
                }
            }
        } else if (progress <= storedProgress) {
            // the user goes left so simply save the new progress(space will be
            // available to other SeekBars)
            mAllProgress[which] = progress;
        }
        Log.d("Switches", String.valueOf(mAllProgress));
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
