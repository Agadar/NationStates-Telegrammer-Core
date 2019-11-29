package com.github.agadar.telegrammer.core.event;

import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.telegrammer.core.Telegrammer;

/**
 * Listener for {@link Telegrammer} events.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface TelegrammerListener {

    void handleTelegramSent(TelegramSentEvent event);

    void handleNoRecipientsFound(NoRecipientsFoundEvent event);

    void handleStoppedSending(StoppedSendingEvent event);

    void handleRecipientRemoved(RecipientRemovedEvent event);

    void handleRecipientsRefreshed(RecipientsRefreshedEvent event);
}
