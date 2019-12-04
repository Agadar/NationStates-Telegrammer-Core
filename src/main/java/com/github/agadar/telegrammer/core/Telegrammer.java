package com.github.agadar.telegrammer.core;

import java.util.Collection;

import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.settings.CoreSettingKey;

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
     * Unregisters existing telegram listeners.
     * 
     * @param listeners The listener to unregister.
     */
    public void removeListener(@NonNull TelegrammerListener... listeners);

    /**
     * Updates a core setting.
     * 
     * @param key   The key of the setting to update.
     * @param value The value to update.
     */
    public void updateSetting(@NonNull CoreSettingKey key, @NonNull Object value);

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
     * Stops sending the telegram to the recipients.
     */
    public void stopSending();
}
