package com.github.agadar.telegrammer.core.telegram;

/**
 * Enumerator for telegram types. As of writing, the known types are: Normal,
 * Campaign, and Recruitment.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public enum TelegramType {
    NORMAL("Normal"),
    CAMPAIGN("Campaign"),
    RECRUITMENT("Recruitment");

    // Explanation.
    private final String text;

    /**
     * Constructor.
     *
     * @param text
     */
    private TelegramType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
