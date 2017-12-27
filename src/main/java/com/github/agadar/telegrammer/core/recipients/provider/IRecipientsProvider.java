package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.HashSet;

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
    public HashSet<String> getRecipients();
}
