package com.github.agadar.telegrammer.core.telegram.sender;

import com.github.agadar.telegrammer.core.telegram.history.ITelegramHistory;
import com.github.agadar.telegrammer.core.propertiesmanager.IPropertiesManager;
import com.github.agadar.nationstates.INationStates;

import com.github.agadar.telegrammer.core.telegram.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.recipients.listbuilder.IRecipientsListBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class TelegramSender implements ITelegramSender {

    private final String userAgentFormat = "Agadar's Telegrammer using Client "
            + "Key '%s' (https://github.com/Agadar/NationStates-Telegrammer)";  // User agent string for formatting.
    private final int noAddresseesFoundTimeout = 60000;               // Duration in milliseconds for timeout when no recipients were found while looping.

    private final Set<TelegramManagerListener> listeners = new HashSet<>(); // Listeners to events thrown by this.

    private Thread telegramThread;                                          // The thread on which the TelegramQuery is running.

    private final INationStates nationStates;
    private final ITelegramHistory historyManager;
    private final IPropertiesManager propertiesManager;

    public TelegramSender(INationStates nationStates, ITelegramHistory historyManager, IPropertiesManager propertiesManager) {
        this.nationStates = nationStates;
        this.historyManager = historyManager;
        this.propertiesManager = propertiesManager;
    }

    @Override
    public void startSending(IRecipientsListBuilder recipientsListBuilder) {
        // Make sure all inputs are valid.
        if (propertiesManager.getClientKey() == null || propertiesManager.getClientKey().isEmpty()) {
            throw new IllegalArgumentException("Please supply a Client Key!");
        }
        if (propertiesManager.getTelegramId() == null || propertiesManager.getTelegramId().isEmpty()) {
            throw new IllegalArgumentException("Please supply a Telegram Id!");
        }
        if (propertiesManager.getSecretKey() == null || propertiesManager.getSecretKey().isEmpty()) {
            throw new IllegalArgumentException("Please supply a Secret Key!");
        }

        // Check to make sure the thread is not already running to prevent synchronization issues.
        if (telegramThread != null && telegramThread.isAlive()) {
            throw new IllegalThreadStateException("Telegram thread already running!");
        }

        // Make sure there is at least one recipient to send the telegram to.
        if (recipientsListBuilder.getRecipients().isEmpty()) {
            throw new IllegalArgumentException("Please supply at least one recipient!");
        }

        // Update user agent.
        nationStates.setUserAgent(String.format(userAgentFormat, propertiesManager.getClientKey()));

        // Prepare the runnable.
        final SendTelegramsRunnable sendTelegramsRunnable
                = new SendTelegramsRunnable(recipientsListBuilder, nationStates, historyManager, propertiesManager, listeners, noAddresseesFoundTimeout);
        telegramThread = new Thread(sendTelegramsRunnable);
        telegramThread.start();
    }

    @Override
    public void stopSending() {
        if (telegramThread != null) {
            telegramThread.interrupt();
            telegramThread = null;
        }
    }

    @Override
    public void addListeners(TelegramManagerListener... newlisteners) {
        synchronized (listeners) {
            listeners.addAll(Arrays.asList(newlisteners));
        }
    }
}
