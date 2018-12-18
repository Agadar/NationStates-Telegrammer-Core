package com.github.agadar.telegrammer.core.recipients.filter;

import java.util.Collection;

/**
 * Null-object for IRecipientsFilters.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NullRecipientsFilter implements RecipientsFilter {

    @Override
    public void applyFilterToRecipients(Collection<String> recipients) {
        /**/
    }

    @Override
    public void refreshFilter() {
        /**/
    }

}
