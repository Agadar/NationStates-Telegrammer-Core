package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

/**
 * Responsible for fetching recipients (nation names) from the official API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface RecipientsProvider {

    /**
     * Fetches the recipients, using any locally supplied parameters.
     *
     * @return The recipients
     */
    public Collection<String> getRecipients();
}
