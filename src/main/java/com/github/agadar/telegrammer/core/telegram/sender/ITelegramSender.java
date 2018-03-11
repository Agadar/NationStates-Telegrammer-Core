package com.github.agadar.telegrammer.core.telegram.sender;

import com.github.agadar.telegrammer.core.telegram.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.recipients.listbuilder.IRecipientsListBuilder;

/**
 * Manages the sending of telegrams.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface ITelegramSender {

    /**
     * Starts sending the telegram to the recipients.
     *
     * @param recipientsListBuilder
     * @throws IllegalArgumentException If the variables are not properly set.
     */
    public void startSending(IRecipientsListBuilder recipientsListBuilder);

    /**
     * Stops sending the telegram to the recipients.
     */
    public void stopSending();

    /**
     * Registers new telegram manager listeners.
     *
     * @param newlisteners
     *            the listeners to register
     */
    public void addListeners(TelegramManagerListener... newlisteners);
}
