package com.github.agadar.telegrammer.core;

import java.util.Collection;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.telegrammer.core.event.TelegrammerListener;
import com.github.agadar.telegrammer.core.progress.ProgressSummary;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.settings.Settings;
import com.github.agadar.telegrammer.core.settings.TelegrammerCoreSettings;

import lombok.NonNull;

/**
 * The starting point for consumers of this telegrammer library for the
 * NationStates API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface Telegrammer {

    /**
     * Initialises this telegrammer, loading and parsing configuration/misc. files
     * to set this telegrammer up properly and initialising the underlying
     * {@link NationStates} component.
     * 
     * @param userAgent The user agent to use for API calls. NationStates moderators
     *                  should be able to identify you and your script via your user
     *                  agent. As such, try providing at least your nation name, and
     *                  preferably include your e-mail address, a link to a website
     *                  you own, or something else that can help them contact you if
     *                  needed.
     * @throws NationStatesAPIException If initializing the underlying
     *                                  {@link NationStates} failed.
     */
    public void initialise(@NonNull String userAgent) throws NationStatesAPIException;

    /**
     * Registers new telegram listeners.
     *
     * @param listeners The listeners to register.
     */
    public void addListeners(@NonNull TelegrammerListener... listeners);

    /**
     * Creates a new {@link RecipientsFilter}.
     * 
     * @param filterType   Type of the filter.
     * @param filterAction Action of the filter.
     * @param input        Arguments for the filter.
     * @return The created recipients filter.
     */
    public RecipientsFilter createFilter(@NonNull RecipientsFilterType filterType,
            @NonNull RecipientsFilterAction filterAction,
            @NonNull Collection<String> input);

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
     * Gets the application-wide settings.
     * 
     * @return The application-wide settings.
     */
    public Settings getSettings();

    /**
     * Gets the telegrammer core specific settings.
     * 
     * @return The telegrammer core specific settings.
     */
    public TelegrammerCoreSettings getTelegrammerCoreSettings();

    /**
     * Gets the underlying NationStates component.
     * 
     * @return The underlying NationStates component.
     */
    public NationStates getNationStates();
}
