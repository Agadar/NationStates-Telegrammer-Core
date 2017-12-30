package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Set;

/**
 * Responsible for fetching recipients (nation names) from the official API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface IRecipientsProvider {

    /**
     * Fetches the recipients, using any locally supplied parameters.
     *
     * @return The recipients
     */
    public Set<String> getRecipients();
}
