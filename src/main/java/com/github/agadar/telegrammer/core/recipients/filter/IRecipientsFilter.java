package com.github.agadar.telegrammer.core.recipients.filter;

import java.util.Set;

/**
 * A filter that can add or remove recipients from a recipient collection.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface IRecipientsFilter {

    /**
     * Applies this filter to a recipient collection.
     *
     * @param recipients
     */
    public void applyFilterToRecipients(Set<String> recipients);

    /**
     * Refreshes this filter, which may involve a call to an external server or
     * something along those lines.
     */
    public void refreshFilter();
}
