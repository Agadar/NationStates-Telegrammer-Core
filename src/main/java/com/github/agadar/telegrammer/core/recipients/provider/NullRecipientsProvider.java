package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;
import java.util.Collections;

/**
 * Our null object for IRecipientsProviders.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NullRecipientsProvider implements RecipientsProvider {

    @Override
    public Collection<String> getRecipients() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "_";
    }

}
