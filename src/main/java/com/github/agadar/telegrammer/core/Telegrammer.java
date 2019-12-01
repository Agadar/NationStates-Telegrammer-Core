package com.github.agadar.telegrammer.core;

import java.util.Collection;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.telegrammer.core.misc.TelegrammerState;
import com.github.agadar.telegrammer.core.progress.ProgressSummary;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.settings.CoreSettings;

import lombok.NonNull;

/**
 * The starting point for consumers of this telegrammer library for the
 * NationStates API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface Telegrammer {

    /**
     * Registers new telegram listeners.
     *
     * @param listeners The listeners to register.
     */
    public void addListeners(@NonNull TelegrammerListener... listeners);

    /**
     * Adds a filter according to the specified parameters.
     * 
     * @param filterType   Type of the filter.
     * @param filterAction Action of the filter.
     * @param input        Arguments for the filter.
     */
    public void addFilter(@NonNull RecipientsFilterType filterType,
            @NonNull RecipientsFilterAction filterAction,
            @NonNull Collection<String> input);

    /**
     * Removes the filter at the specified index.
     * 
     * @param index The index of the filter to remove.
     */
    public void removeFilterAtIndex(int index);

    /**
     * Refreshes the filters.
     */
    public void refreshFilters();

    /**
     * Starts sending the telegram to the recipients.
     */
    public void startSending();

    /**
     * Gets a summary of the current telegram queuing progress.
     * 
     * @return A summary of the current telegram queuing progress.
     */
    public ProgressSummary getProgressSummary();

    /**
     * Stops sending the telegram to the recipients.
     */
    public void stopSending();

    /**
     * Gets the telegrammer core specific settings.
     * 
     * @return The telegrammer core specific settings.
     */
    public CoreSettings getCoreSettings();

    /**
     * Gets the underlying NationStates component.
     * 
     * @return The underlying NationStates component.
     */
    public NationStates getNationStates();

    /**
     * Gets the current state of the telegrammer library.
     * 
     * @return The current state of the telegrammer library.
     */
    public TelegrammerState getState();
}
