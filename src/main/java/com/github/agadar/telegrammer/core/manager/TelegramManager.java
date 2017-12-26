package com.github.agadar.telegrammer.core.manager;

import com.github.agadar.nationstates.INationStates;

import com.github.agadar.telegrammer.core.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.filter.abstractfilter.Filter;
import com.github.agadar.telegrammer.core.runnable.SendTelegramsRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TelegramManager implements ITelegramManager {

    private final String userAgentFormat = "Agadar's Telegrammer using Client "
            + "Key '%s' (https://github.com/Agadar/NationStates-Telegrammer)";  // User agent string for formatting.
    private final int noAddresseesFoundTimeout = 60000;               // Duration in milliseconds for timeout when no recipients were found while looping.

    private final List<Filter> filters = new ArrayList<>();                 // The filters to apply, in chronological order.
    private final Set<String> recipients = new HashSet<>();                 // Supposedly most up-to-date recipients list, based on Filters.
    private final Set<TelegramManagerListener> listeners = new HashSet<>(); // Listeners to events thrown by this.

    private Thread telegramThread;                                          // The thread on which the TelegramQuery is running.

    private final INationStates nationStates;
    private final IHistoryManager historyManager;
    private final IPropertiesManager propertiesManager;

    public TelegramManager(INationStates nationStates, IHistoryManager historyManager, IPropertiesManager propertiesManager) {
        this.nationStates = nationStates;
        this.historyManager = historyManager;
        this.propertiesManager = propertiesManager;
    }

    @Override
    public boolean cantRetrieveMoreNations() {
        return filters.stream().noneMatch((filter) -> (!filter.cantRetrieveMoreNations()));
    }

    @Override
    public boolean potentiallyInfinite() {
        return filters.stream().anyMatch(filter -> filter.potentiallyInfinite());
    }

    @Override
    public void addFilter(Filter filter) {
        filter.refresh();
        filter.applyFilter(recipients);
        filters.add(filter);
    }

    @Override
    public int numberOfRecipients() {
        return recipients.size();
    }

    @Override
    public Set<String> getRecipients() {
        return new HashSet<>(recipients);
    }

    @Override
    public void resetAndReapplyFilters() {
        recipients.clear();
        filters.forEach((filter) -> {
            filter.reset();
            //filter.refresh();
            filter.applyFilter(recipients);
        });
    }

    @Override
    public void refreshAndReapplyFilters() {
        recipients.clear();
        filters.forEach((filter) -> {
            filter.refresh();
            filter.applyFilter(recipients);
        });
    }

    @Override
    public void removeFilterAt(int index) {
        filters.remove(index);
        recipients.clear();
        filters.forEach((filter) -> {
            filter.reset();
            filter.applyFilter(recipients);
        });
    }

    @Override
    public void startSending(boolean nonblocking) {
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
        if (numberOfRecipients() == 0 && cantRetrieveMoreNations()) {
            throw new IllegalArgumentException("Please supply at least one recipient!");
        }

        // Update user agent.
        nationStates.setUserAgent(String.format(userAgentFormat, propertiesManager.getClientKey()));

        // Prepare the runnable.
        final SendTelegramsRunnable sendTelegramsRunnable
                = new SendTelegramsRunnable(this, nationStates, historyManager, propertiesManager, recipients, listeners, noAddresseesFoundTimeout);

        // Depending on the 'nonblocking' choice, either run the runnable in a new thread,
        // or just call its start() method on this thread.
        if (nonblocking) {
            telegramThread = new Thread(sendTelegramsRunnable);
            telegramThread.start();
        } else {
            sendTelegramsRunnable.run();
        }
    }

    @Override
    public void stopSending() {
        if (telegramThread != null) {
            telegramThread.interrupt();
        }
    }

    @Override
    public void addListeners(TelegramManagerListener... newlisteners) {
        synchronized (listeners) {
            listeners.addAll(Arrays.asList(newlisteners));
        }
    }
}
