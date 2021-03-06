package com.github.agadar.telegrammer.core.recipients.listbuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;

/**
 * Assists in building a list of recipients, using filters and telegram history.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface RecipientsListBuilder {

    /**
     * @return The result of applying all filters for the specified telegram id,
     *         representing a recipients set.
     * @param telegramId
     * @return A collection of recipients for the specified telegram id.
     */
    public Collection<String> getRecipients(String telegramId);

    /**
     * @return The filters of this builder.
     */
    public List<RecipientsFilter> getFilters();

    /**
     * Appends a new filter to the end of this chain.
     *
     * @param filter
     * @return The index of the added filter.
     */
    public int addFilter(RecipientsFilter filter);

    /**
     * Refreshes all filters.
     * 
     * @return A map of filters that failed with an error while refreshing.
     */
    public Map<RecipientsFilter, NationStatesAPIException> refreshFilters();

    /**
     * Removes the filter at the specified index.
     *
     * @param index
     */
    public void removeFilterAt(int index);

    /**
     * Clears this filter chain.
     */
    public void resetFilters();

    /**
     * Gets the configuration string representation of this builder.
     * 
     * @return The configuration string representation of this builder.
     */
    public String toConfigurationString();
}
