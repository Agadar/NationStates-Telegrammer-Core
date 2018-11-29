package com.github.agadar.telegrammer.core.telegram.event;

import com.github.agadar.telegrammer.core.telegram.SkippedRecipientReason;

import lombok.Getter;

import java.util.EventObject;

/**
 * Published by TelegramManager when a recipient was removed from the recipients
 * set.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Getter
public class RecipientRemovedEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    /**
     * The recipient in question.
     */
    private final String recipient;

    /**
     * The reason why the recipient was removed.
     */
    private final SkippedRecipientReason reason;

    public RecipientRemovedEvent(Object source, String recipient, SkippedRecipientReason reason) {
        super(source);
        this.recipient = recipient;
        this.reason = reason;
    }
}
