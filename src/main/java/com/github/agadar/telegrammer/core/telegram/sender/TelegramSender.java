package com.github.agadar.telegrammer.core.telegram.sender;

import com.github.agadar.telegrammer.core.settings.TelegrammerSettings;
import com.github.agadar.telegrammer.core.telegram.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.telegram.progress.ProgressSummary;

/**
 * Manages the sending of telegrams.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface TelegramSender {

    /**
     * Starts sending the telegram to the recipients.
     *
     * @param settings The settings to use for sending telegrams.
     */
    public void startSending(TelegrammerSettings settings);

    /**
     * Stops sending the telegram to the recipients.
     */
    public void stopSending();

    /**
     * Registers new telegram manager listeners.
     *
     * @param newlisteners the listeners to register
     */
    public void addListeners(TelegramManagerListener... newlisteners);

    /**
     * Gets a summary of the current telegram queuing progress.
     * 
     * @return A summary of the current telegram queuing progress.
     */
    public ProgressSummary getProgressSummary();
}
