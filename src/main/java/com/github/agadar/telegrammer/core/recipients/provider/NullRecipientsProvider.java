package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;
import java.util.Collections;

import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

/**
 * Our null object for RecipientsProviders.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NullRecipientsProvider extends RecipientsProvider {

    public NullRecipientsProvider() {
        super(RecipientsFilterType.ALL_NATIONS);
    }

    @Override
    public Collection<String> getRecipients() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "_";
    }

}
