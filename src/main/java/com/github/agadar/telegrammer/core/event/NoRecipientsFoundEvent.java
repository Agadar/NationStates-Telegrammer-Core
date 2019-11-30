package com.github.agadar.telegrammer.core.event;

import java.util.EventObject;

import com.github.agadar.telegrammer.core.Telegrammer;

import lombok.Getter;

/**
 * Fired by the {@link Telegrammer} when it is looping to send telegrams but did
 * not find any valid recipients to send the telegram to.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Getter
public class NoRecipientsFoundEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    /**
     * Duration of the time-out in milliseconds.
     */
    private final int timeOut;

    /**
     * @param source
     * @param timeout Duration of the time-out in milliseconds.
     */
    public NoRecipientsFoundEvent(Object source, int timeout) {
        super(source);
        this.timeOut = timeout;
    }
}
