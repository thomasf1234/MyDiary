package com.abstractx1.mydiary.record;

import java.io.File;
import java.io.IOException;

/**
 * Created by tfisher on 17/11/2016.
 */

public class Recorder {
    private Microphone microphone;
    private File outputFile;
    private Stopwatch stopwatch;

    public Recorder() throws IOException {
        this.microphone = new Microphone();
        this.stopwatch = new Stopwatch();
        createAndSetOutputFile();
    }

    public void record() throws IOException {
        stopwatch.start();
        microphone.record();
    }

    public void stop() {
        microphone.finish();
        stopwatch.stop();
    }

    public float getRecordingDuration() {
        return stopwatch.getElapsedTimeSeconds();
    }

    public int getRecordingDurationMilliSeconds() {
        return stopwatch.getElapsedTimeMilliSeconds();
    }

    public void reset() throws IOException {
        outputFile.delete();
        createAndSetOutputFile();
    }

    public File getOutputFile() {
        return outputFile;
    }

    private void createAndSetOutputFile() throws IOException {
        outputFile = File.createTempFile("microphone_recording", ".3gp");
        outputFile.deleteOnExit();
        microphone.setOutputFile(outputFile.getAbsolutePath());
    }
}
