package com.github.agadar.telegrammer.core.telegram.event;

import com.github.agadar.telegrammer.core.telegram.SkippedRecipientReason;
import java.util.EventObject;

/**
 * Published by TelegramManager when a recipient was removed from the recipients
 * set.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RecipientRemovedEvent extends EventObject {

    /**
     * The recipient in question.
     */
    public final String recipient;

    /**
     * The reason why the recipient was removed.
     */
    public final SkippedRecipientReason reason;

    public RecipientRemovedEvent(Object source, String recipient, SkippedRecipientReason reason) {
	super(source);
	this.recipient = recipient;
	this.reason = reason;
    }
}
