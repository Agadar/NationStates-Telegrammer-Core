package com.github.agadar.telegrammer.core.recipients.filter;

import java.util.Collection;

/**
 * Null-object for RecipientsFilters.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NullRecipientsFilter extends RecipientsFilter {

    public NullRecipientsFilter() {
        super(RecipientsFilterAction.ADD_TO_RECIPIENTS);
    }

    @Override
    public void applyFilterToRecipients(Collection<String> recipients) {
        /**/
    }

    @Override
    public void refreshFilter() {
        /**/
    }

    @Override
    public String toConfigurationString() {
        return "";
    }

}
