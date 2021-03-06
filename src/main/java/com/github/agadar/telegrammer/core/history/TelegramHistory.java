package com.github.agadar.telegrammer.core.history;

import java.util.Collection;

import com.github.agadar.telegrammer.core.misc.SkippedRecipientReason;

/**
 * Assists in saving and loading the history file for this application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface TelegramHistory {

    /**
     * Gets the SkippedRecipientReason mapped to the given telegramId and recipient.
     *
     * @param telegramId
     * @param recipient
     * @return The SkippedRecipientReason mapped to the given telegramId and
     *         recipient, otherwise null.
     */
    public SkippedRecipientReason getSkippedRecipientReason(String telegramId, String recipient);

    /**
     * Saves a new entry in the telegram history and persists it to the history
     * file.
     *
     * @param telegramId Id of the sent telegram.
     * @param recipient  Recipient of the sent telegram.
     * @param reason     The reason for storing in history.
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
     * Removes recipients from the supplied collection that are invalid for the
     * specified telegram id.
     *
     * @param nations
     * @param telegramId
     */
    public void removeOldRecipients(Collection<String> nations, String telegramId);
}
