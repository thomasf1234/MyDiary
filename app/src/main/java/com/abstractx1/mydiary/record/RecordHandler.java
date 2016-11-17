package com.abstractx1.mydiary.record;

import com.abstractx1.mydiary.lib.states.State4;

import java.io.IOException;

/**
 * Created by tfisher on 16/11/2016.
 */

/*
State.IDLE
Sate.RECORDING
State.PLAYING
State.PAUSED
 */
public class RecordHandler extends State4 {
    private State IDLE = State.ONE;
    private State RECORDING = State.TWO;
    private State PLAYING = State.THREE;
    private State PAUSED = State.FOUR;

    public RecordHandler() throws Exception {
        super();
    }

    @Override
    protected void onSetStateONE() throws Exception {
      throw new IOException("jj");
    }
}




