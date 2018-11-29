package com.github.agadar.telegrammer.core.telegram.sender;

import com.github.agadar.telegrammer.core.telegram.history.ITelegramHistory;

import lombok.NonNull;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.telegrammer.core.properties.ApplicationProperties;

import com.github.agadar.telegrammer.core.telegram.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.recipients.listbuilder.IRecipientsListBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TelegramSender implements ITelegramSender {

    private final String userAgentFormat = "Agadar's Telegrammer using Client "
            + "Key '%s' (https://github.com/Agadar/NationStates-Telegrammer)"; // User agent string for formatting.
    private final int noAddresseesFoundTimeout = 60000; // Duration in milliseconds for timeout when no recipients were
                                                        // found while looping.

    private final Set<TelegramManagerListener> listeners = new HashSet<>(); // Listeners to events thrown by this.

    private Thread telegramThread; // The thread on which the TelegramQuery is running.

    private final INationStates nationStates;
    private final ITelegramHistory historyManager;
    private final ApplicationProperties properties;

    public TelegramSender(@NonNull INationStates nationStates, @NonNull ITelegramHistory historyManager,
            @NonNull ApplicationProperties properties) {
        this.nationStates = nationStates;
        this.historyManager = historyManager;
        this.properties = properties;
    }

    @Override
    public void startSending(@NonNull IRecipientsListBuilder recipientsListBuilder) {
        // Make sure all inputs are valid.
        if (properties.getClientKey() == null || properties.getClientKey().isEmpty()) {
            throw new IllegalArgumentException("Please supply a Client Key!");
        }
        if (properties.getTelegramId() == null || properties.getTelegramId().isEmpty()) {
            throw new IllegalArgumentException("Please supply a Telegram Id!");
        }
        if (properties.getSecretKey() == null || properties.getSecretKey().isEmpty()) {
            throw new IllegalArgumentException("Please supply a Secret Key!");
        }

        // Check to make sure the thread is not already running to prevent
        // synchronization issues.
        if (telegramThread != null && telegramThread.isAlive()) {
            throw new IllegalThreadStateException("Telegram thread already running!");
        }

        // Make sure there is at least one recipient to send the telegram to.
        if (recipientsListBuilder.getRecipients().isEmpty()) {
            throw new IllegalArgumentException("Please supply at least one recipient!");
        }

        // Update user agent.
        nationStates.setUserAgent(String.format(userAgentFormat, properties.getClientKey()));

        // Prepare the runnable.
        final SendTelegramsRunnable sendTelegramsRunnable = new SendTelegramsRunnable(recipientsListBuilder,
                nationStates, historyManager, properties, listeners, noAddresseesFoundTimeout);
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
    public void addListeners(@NonNull TelegramManagerListener... newlisteners) {
        synchronized (listeners) {
            listeners.addAll(Arrays.asList(newlisteners));
        }
    }
}
