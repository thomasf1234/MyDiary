package com.abstractx1.mydiary.lib.states;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tfisher on 17/11/2016.
 */

public abstract class State6 {
    public enum State { ONE, TWO, THREE, FOUR, FIVE, SIX }

    protected State state;

    public synchronized void transitionTo(State toState) throws Exception {
        if(state == null)
            setState(toState);
        else if(getValidStateTransitions().containsKey(state)
                && Arrays.asList(getValidStateTransitions().get(state)).contains(toState)) {
            setState(toState);
        } else {
            throw new IllegalStateTransitionException("Invalid transition: { Class: " + getClass() + ", (" + state.name() + " to " + toState.name() + ") }");
        }
    }

    private void setState(State state) throws Exception {
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

    protected abstract Map<State, State[]> getValidStateTransitions();
}
