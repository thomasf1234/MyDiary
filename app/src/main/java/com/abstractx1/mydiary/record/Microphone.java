package com.abstractx1.mydiary.record;

/**
 * Created by tfisher on 16/11/2016.
 */

import android.media.MediaRecorder;

import java.io.IOException;

public class Microphone extends MediaRecorder {
    public Microphone() throws IOException {
        initialize();
    }

    public void record() throws IOException {
        prepare();
        start();
    }

    public void finish() {
        stop();
    }

    public void restart() {
        reset();
        initialize();
    }

    private void initialize() {
        setAudioSource(MediaRecorder.AudioSource.MIC);
        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
    }
}
