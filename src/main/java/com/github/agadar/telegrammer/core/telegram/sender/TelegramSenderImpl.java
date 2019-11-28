package com.github.agadar.telegrammer.core.telegram.sender;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.telegrammer.core.settings.TelegrammerSettings;
import com.github.agadar.telegrammer.core.telegram.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.telegram.history.TelegramHistory;
import com.github.agadar.telegrammer.core.telegram.progress.ProgressSummary;

import lombok.NonNull;

public class TelegramSenderImpl implements TelegramSender {

    private final String userAgentFormat = "Agadar's Telegrammer using Client Key '%s' (https://github.com/Agadar/NationStates-Telegrammer)";
    private final int noAddresseesFoundTimeout = 60000;
    private final Collection<TelegramManagerListener> listeners = new HashSet<>();

    private final NationStates nationStates;
    private final TelegramHistory historyManager;

    private Thread telegramThread;
    private SendTelegramsRunnable sendTelegramsRunnable;

    public TelegramSenderImpl(@NonNull NationStates nationStates, @NonNull TelegramHistory historyManager) {
        this.nationStates = nationStates;
        this.historyManager = historyManager;
    }

    @Override
    public void startSending(@NonNull TelegrammerSettings settings) {

        // Make sure all inputs are valid.
        if (settings.getClientKey().isEmpty()) {
            throw new IllegalArgumentException("Please supply a Client Key!");
        }
        if (settings.getTelegramId().isEmpty()) {
            throw new IllegalArgumentException("Please supply a Telegram Id!");
        }
        if (settings.getSecretKey().isEmpty()) {
            throw new IllegalArgumentException("Please supply a Secret Key!");
        }

        // Check to make sure the thread is not already running to prevent
        // synchronization issues.
        if (telegramThread != null && telegramThread.isAlive()) {
            throw new IllegalThreadStateException("Telegram thread already running!");
        }

        // Make sure there is at least one recipient to send the telegram to.
        if (settings.getFilters().getRecipients(settings.getTelegramId()).isEmpty()) {
            throw new IllegalArgumentException("Please supply at least one recipient!");
        }

        // Update user agent.
        nationStates.setUserAgent(String.format(userAgentFormat, settings.getClientKey()));

        // Prepare the runnable.
        sendTelegramsRunnable = new SendTelegramsRunnable(listeners, noAddresseesFoundTimeout, nationStates,
                historyManager, settings);
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

    @Override
    public ProgressSummary getProgressSummary() {
        return sendTelegramsRunnable.getProgressSummary();
    }
}
