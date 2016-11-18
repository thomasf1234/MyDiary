package com.abstractx1.mydiary.record;

import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;

/**
 * Created by tfisher on 18/11/2016.
 */

public class RecordingPlayer extends MediaPlayer {
    public void play() {
        start();
    }

    public float getPlayingDuration() {
        return getCurrentPosition() / 1000f;
    }

    public void setInputFile(File file) throws IOException {
        setDataSource(file.getAbsolutePath());
        prepare();
    }
}
