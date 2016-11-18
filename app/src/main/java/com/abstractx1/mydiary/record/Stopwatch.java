package com.abstractx1.mydiary.record;

/**
 * Created by tfisher on 17/11/2016.
 */

public class Stopwatch {
    private long startTime;
    private long elapsedTime;

    public Stopwatch() {
        reset();
    }

    public void reset() {
        this.elapsedTime = 0;
        this.startTime = 0;
    }

    public void start() {
        reset();
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        if (isRunning())
            this.elapsedTime = millisecondsElapsed();
    }

    public float getElapsedTimeSeconds() {
        return getElapsedTimeMilliSeconds() / 1000f;
    }

    public int getElapsedTimeMilliSeconds() {
        if (elapsedTime > 0)
            return (int) elapsedTime;
        else
            return millisecondsElapsed();
    }

    private boolean isRunning() {
        return elapsedTime == 0 && startTime > 0;
    }

    private int millisecondsElapsed() {
        return (int) (System.currentTimeMillis() - startTime);
    }
}