package com.github.agadar.telegrammer.core.recipients.filter;

import java.util.Set;

/**
 * Null-object for IRecipientsFilters.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NullRecipientsFilter implements IRecipientsFilter {

    @Override
    public void applyFilterToRecipients(Set<String> recipients) {
        /**/
    }

    @Override
    public void refreshFilter() {
        /**/
    }

}
