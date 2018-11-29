package com.github.agadar.telegrammer.core.telegram.event;

import java.util.EventObject;

import lombok.Getter;

/**
 * Fired by the TelegramManager when it is looping but did not find any valid
 * addressees to send the telegram to.
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
