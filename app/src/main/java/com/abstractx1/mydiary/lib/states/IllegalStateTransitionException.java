package com.abstractx1.mydiary.lib.states;

/**
 * Created by tfisher on 18/11/2016.
 */

public class IllegalStateTransitionException extends RuntimeException {
    public IllegalStateTransitionException(String message) {
        super(message);
    }
}
