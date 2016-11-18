package com.abstractx1.mydiary.record;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by tfisher on 18/11/2016.
 */

public class RecordingPlayer extends MediaPlayer {
    private Stopwatch stopwatch;

    public RecordingPlayer() {
        this.stopwatch = new Stopwatch();
    }

    public void play() throws IOException {
        stopwatch.start();
        prepare();
        start();
    }

    public float getPlayingDuration() {
        return stopwatch.getElapsedTime();
    }
}
