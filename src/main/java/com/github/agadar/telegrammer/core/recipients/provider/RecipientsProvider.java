package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Responsible for fetching recipients (nation names) from the official API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@AllArgsConstructor
public abstract class RecipientsProvider {

    @NonNull
    protected final RecipientsFilterType filterType;

    /**
     * Fetches the recipients, using any locally supplied parameters.
     *
     * @return The recipients
     */
    public abstract Collection<String> getRecipients();

    /**
     * Gets the configuration string representation of this provider.
     * 
     * @return The configuration string representation of this provider.
     */
    public String toConfigurationString() {
        return filterType.name();
    }

    @Override
    public String toString() {
        return filterType.toString();
    }
}
