package com.github.agadar.telegrammer.core.recipientsfilter;

import java.util.HashSet;

/**
 * Assists in building a list of recipients, using filters and telegram history.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface IRecipientsListBuilder {

    /**
     * @return The result of applying all filters, representing a recipients
     * set.
     */
    public HashSet<String> getRecipients();

    /**
     * Appends a new filter to the end of this chain.
     *
     * @param filter
     * @return The index of the added filter.
     */
    public int addFilter(IRecipientsFilter filter);

    /**
     * Refreshes all filters.
     */
    public void refreshFilters();

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
}
