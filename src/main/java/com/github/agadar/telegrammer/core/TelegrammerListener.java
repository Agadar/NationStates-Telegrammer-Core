package com.github.agadar.telegrammer.core;

import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.telegrammer.core.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.event.RecipientsRefreshedEvent;
import com.github.agadar.telegrammer.core.event.StartedCompilingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.StartedSendingEvent;
import com.github.agadar.telegrammer.core.event.StoppedCompilingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.StoppedSendingEvent;

/**
 * Listener for {@link Telegrammer} events.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface TelegrammerListener {

    void handleStartedCompilingRecipients(StartedCompilingRecipientsEvent event);

    void handleFinishedCompilingRecipients(StoppedCompilingRecipientsEvent event);

    void handleStartedSending(StartedSendingEvent event);

    void handleTelegramSent(TelegramSentEvent event);

    void handleNoRecipientsFound(NoRecipientsFoundEvent event);

    void handleRecipientRemoved(RecipientRemovedEvent event);

    void handleRecipientsRefreshed(RecipientsRefreshedEvent event);

    void handleStoppedSending(StoppedSendingEvent event);
}
