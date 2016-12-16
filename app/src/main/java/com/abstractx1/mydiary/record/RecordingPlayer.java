package com.abstractx1.mydiary.record;

import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;

/**
 * Created by tfisher on 18/11/2016.
 */

public class RecordingPlayer extends MediaPlayer {
    enum State {
        EMPTY, PREPARED, PLAYING, PAUSED, RELEASED
    }

    private State state;

    public RecordingPlayer() {
        super();
        setState(State.EMPTY);
    }

    public void setState(State state) {
        this.state = state;
    }


    public void play() {
        start();
        setState(State.PLAYING);
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        setState(State.PAUSED);
    }

    public void release() {
        super.release();
        setState(State.RELEASED);
    }

    public float getPlayingDuration() {
        return getCurrentPosition() / 1000f;
    }

    public void setInputFile(File file) throws IOException {
        setDataSource(file.getAbsolutePath());
        prepare();
        setState(State.PREPARED);
    }

    public void cancel() {
        if (state != State.RELEASED) {
            if (isPlaying()) {
                stop();
            }
            release();
        }
    }
}
