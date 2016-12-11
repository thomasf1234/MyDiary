package com.abstractx1.mydiary.record;

/**
 * Created by tfisher on 16/11/2016.
 */

import android.media.MediaRecorder;

import java.io.IOException;

public class Microphone extends MediaRecorder {
    enum State {
        NOT_RECORDING, RECORDING
    }

    private State state;

    public Microphone() throws IOException {
        initialize();
    }

    public void record() throws IOException {
        setState(State.RECORDING);
        prepare();
        start();
    }

    public void finish() {
        stop();
        setState(State.NOT_RECORDING);
    }

    public boolean isRecording(){
        return this.state == State.RECORDING;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void restart() {
        reset();
        initialize();
    }

    private void initialize() {
        setAudioSource(AudioSource.MIC);
        setOutputFormat(OutputFormat.MPEG_4);
        setAudioEncoder(AudioEncoder.AAC);
        this.state = State.NOT_RECORDING;
    }
}
