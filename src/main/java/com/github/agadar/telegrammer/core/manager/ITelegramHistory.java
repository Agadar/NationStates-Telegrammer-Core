package com.github.agadar.telegrammer.core.manager;

import com.github.agadar.telegrammer.core.enums.SkippedRecipientReason;

import java.util.Collection;

/**
 * Assists in saving and loading the history file for this application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface ITelegramHistory {

    /**
     * Gets the SkippedRecipientReason mapped to the given telegramId and
     * recipient.
     *
     * @param telegramId
     * @param recipient
     * @return The SkippedRecipientReason mapped to the given telegramId and
     * recipient, otherwise null.
     */
    public SkippedRecipientReason getSkippedRecipientReason(String telegramId, String recipient);

    /**
     * Saves a new entry in the telegram history and persists it to the history
     * file.
     *
     * @param telegramId Id of the sent telegram.
     * @param recipient Recipient of the sent telegram.
     * @param reason The reason for storing in history.
     * @return True if successful, otherwise false.
     */
    public boolean saveHistory(String telegramId, String recipient, SkippedRecipientReason reason);

    /**
     * Loads the application's history data from the file.
     *
     * @return True if loading succeeded, false otherwise.
     */
    public boolean loadHistory();

    /**
     * Removes invalid recipients from the supplied set.
     *
     * @param nations
     */
    public void removeOldRecipients(Collection<String> nations);
}
