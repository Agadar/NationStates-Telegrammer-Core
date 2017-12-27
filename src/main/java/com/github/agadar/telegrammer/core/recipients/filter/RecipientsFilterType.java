package com.github.agadar.telegrammer.core.recipients.filter;

/**
 * The different types of recipient filters.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public enum RecipientsFilterType {
    ADD_TO_RECIPIENTS("Add to recipients"),
    REMOVE_FROM_RECIPIENTS("Remove from recipients"),
    REMOVE_RECIPIENTS_NOT_IN("Remove from recipients not in");

    // Explanation.
    private final String text;

    /**
     * Constructor.
     *
     * @param text
     */
    private RecipientsFilterType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
