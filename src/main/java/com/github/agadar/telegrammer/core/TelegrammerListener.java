package com.github.agadar.telegrammer.core;

import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.telegrammer.core.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.event.FilterRemovedEvent;
import com.github.agadar.telegrammer.core.event.FinishedRefreshingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.StartedRefreshingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.StartedSendingEvent;
import com.github.agadar.telegrammer.core.event.StoppedSendingEvent;

/**
 * Listener for {@link Telegrammer} events.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface TelegrammerListener {

    void handleStartedRefreshingRecipients(StartedRefreshingRecipientsEvent event);

    void handleFinishedRefreshingRecipients(FinishedRefreshingRecipientsEvent event);

    void handleFilterRemoved(FilterRemovedEvent event);

    void handleStartedSending(StartedSendingEvent event);

    void handleTelegramSent(TelegramSentEvent event);

    void handleNoRecipientsFound(NoRecipientsFoundEvent event);

    void handleRecipientRemoved(RecipientRemovedEvent event);

    void handleStoppedSending(StoppedSendingEvent event);

}
