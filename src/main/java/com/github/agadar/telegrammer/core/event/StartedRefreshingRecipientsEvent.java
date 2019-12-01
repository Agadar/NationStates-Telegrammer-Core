package com.github.agadar.telegrammer.core.event;

import java.util.EventObject;

import com.github.agadar.telegrammer.core.Telegrammer;
import com.github.agadar.telegrammer.core.misc.TelegrammerState;

import lombok.Getter;

/**
 * Event fired by {@link Telegrammer} when the library has begun refreshing the
 * recipients.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class StartedRefreshingRecipientsEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    /**
     * The state the {@link Telegrammer} is in when this event was fired.
     */
    @Getter
    private final TelegrammerState telegrammerState;

    public StartedRefreshingRecipientsEvent(Object source, TelegrammerState telegrammerState) {
        super(source);
        this.telegrammerState = telegrammerState;
    }

}
