package com.github.agadar.telegrammer.core.recipients.filter;

import java.util.Collection;

import com.github.agadar.nationstates.exception.NationStatesAPIException;

import lombok.Getter;
import lombok.NonNull;

/**
 * A filter that can add or remove recipients from a recipient collection.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class RecipientsFilter {

    @Getter
    protected final RecipientsFilterAction filterAction;

    /**
     * Constructor.
     * 
     * @param filterAction The action this filter should apply.
     */
    public RecipientsFilter(@NonNull RecipientsFilterAction filterAction) {
        this.filterAction = filterAction;
    }

    /**
     * Applies this filter to a recipient collection.
     *
     * @param recipients The recipients to apply this filter upon.
     */
    public abstract void applyFilterToRecipients(@NonNull Collection<String> recipients);

    /**
     * Refreshes this filter, which may involve a call to an external server or
     * something along those lines.
     * 
     * @throws NationStatesAPIException If something went wrong while calling the
     *                                  NationStates API.
     */
    public abstract void refreshFilter() throws NationStatesAPIException;

    /**
     * Gets the configuration string representation of this filter.
     * 
     * @return The configuration string representation of this filter.
     */
    public String toConfigurationString() {
        return filterAction.name();
    }

    @Override
    public String toString() {
        return filterAction.getPrefix();
    }
}
