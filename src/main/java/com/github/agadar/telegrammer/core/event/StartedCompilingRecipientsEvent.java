package com.github.agadar.telegrammer.core.event;

import java.util.EventObject;

import com.github.agadar.telegrammer.core.Telegrammer;

/**
 * Event fired by {@link Telegrammer} when the library has begun compiling the
 * recipients.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class StartedCompilingRecipientsEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    public StartedCompilingRecipientsEvent(Object source) {
        super(source);
    }

}
