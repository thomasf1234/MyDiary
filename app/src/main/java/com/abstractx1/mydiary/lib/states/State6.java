package com.abstractx1.mydiary.lib.states;


/**
 * Created by tfisher on 17/11/2016.
 */

public abstract class State6 {
    public enum State { ONE, TWO, THREE, FOUR, FIVE, SIX }

    private State state;

    public void setState(State state) throws Exception {
        this.state = state;
        switch (state) {
            case ONE:
                onSetStateONE();
                break;
            case TWO:
                onSetStateTWO();
                break;
            case THREE:
                onSetStateTHREE();
                break;
            case FOUR:
                onSetStateFOUR();
                break;
            case FIVE:
                onSetStateFIVE();
                break;
            case SIX:
                onSetStateSIX();
                break;

        }
    }

    public State getState() {
        return state;
    }


    protected void onSetStateONE() throws Exception {

    }

    protected void onSetStateTWO() throws Exception {

    }

    protected void onSetStateTHREE() throws Exception {

    }

    protected void onSetStateFOUR() throws Exception {

    }

    protected void onSetStateFIVE() throws Exception {

    }

    protected void onSetStateSIX() throws Exception {

    }
}
