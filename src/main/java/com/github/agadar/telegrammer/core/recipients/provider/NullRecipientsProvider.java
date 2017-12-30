package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.HashSet;
import java.util.Set;

/**
 * Our null object for IRecipientsProviders.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NullRecipientsProvider implements IRecipientsProvider {

    @Override
    public Set<String> getRecipients() {
        return new HashSet<>();
    }

    @Override
    public String toString() {
        return "_";
    }

}
