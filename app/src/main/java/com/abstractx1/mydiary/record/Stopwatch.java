package com.abstractx1.mydiary.record;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tfisher on 17/11/2016.
 */

public class Stopwatch {
    private Timer timer;
    private int elapsedTime;

    private void reset() {
        this.timer = new Timer();
        this.elapsedTime = 0;
    }

    public void start() {
        reset();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                incrementRecordingDuration();
            }

        }, 0, 1000);
    }

    public void stop() {
        timer.cancel();
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    private void incrementRecordingDuration() {
        this.elapsedTime += 1;
    }
}
