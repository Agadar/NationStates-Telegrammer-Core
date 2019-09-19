package com.github.agadar.telegrammer.core.recipients.filter;

import lombok.Getter;
import lombok.NonNull;

/**
 * The different types of recipient actions.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public enum RecipientsFilterAction {

    ADD_TO_RECIPIENTS("(+)", "(+) Add to recipients"),
    REMOVE_FROM_RECIPIENTS("(-)", "(-) Remove from recipients"),
    REMOVE_RECIPIENTS_NOT_IN("(!)", "(!) Remove from recipients not in");

    @Getter private final String prefix;
    private final String text;

    private RecipientsFilterAction(@NonNull String prefix, @NonNull String text) {
        this.prefix = prefix;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
