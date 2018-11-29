package com.github.agadar.telegrammer.core.telegram.event;

import java.util.EventObject;

/**
 * Published by TelegramManager when the recipients set is refreshed for the
 * next loop.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RecipientsRefreshedEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    public RecipientsRefreshedEvent(Object source) {
        super(source);
    }
}
