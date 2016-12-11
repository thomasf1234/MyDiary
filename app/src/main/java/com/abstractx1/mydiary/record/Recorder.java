package com.abstractx1.mydiary.record;

import java.io.File;
import java.io.IOException;

/**
 * Created by tfisher on 17/11/2016.
 */

public class Recorder {
    private Microphone microphone;
    private File outputFile;
    private String outputFileName;
    private Stopwatch stopwatch;

    public Recorder(String outputFileName) throws IOException {
        this.microphone = new Microphone();
        this.stopwatch = new Stopwatch();
        this.outputFileName = outputFileName;
        createAndSetOutputFile();
    }

    public void record() throws IOException {
        microphone.record();
        stopwatch.start();
    }

    public void stop() {
        stopwatch.stop();
        microphone.finish();
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

    public boolean isRecording() {
        return microphone.isRecording();
    }

    public void release() {
        microphone.release();
    }

    public File getOutputFile() {
        return outputFile;
    }

    private void createAndSetOutputFile() throws IOException {
        outputFile = File.createTempFile(outputFileName, ".mp4");
        outputFile.deleteOnExit();
        microphone.setOutputFile(outputFile.getAbsolutePath());
    }
}
