package com.github.agadar.telegrammer.core.event;

import java.util.EventObject;

import com.github.agadar.telegrammer.core.Telegrammer;

/**
 * Fires when the {@link Telegrammer} has begun queuing telegrams.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class StartedSendingEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    public StartedSendingEvent(Object source) {
        super(source);
    }

}
