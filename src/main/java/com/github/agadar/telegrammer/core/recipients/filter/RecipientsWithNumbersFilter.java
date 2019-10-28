package com.github.agadar.telegrammer.core.recipients.filter;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Implementation of {@link RecipientsFilter} that removes recipients with
 * numbers in their names.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class RecipientsWithNumbersFilter extends RecipientsFilter {

    private final Pattern numbersPattern = Pattern.compile("\\d");

    public RecipientsWithNumbersFilter() {
        super(RecipientsFilterAction.REMOVE_FROM_RECIPIENTS);
    }

    @Override
    public void applyFilterToRecipients(Collection<String> recipients) {
        recipients.removeIf(numbersPattern.asPredicate());
    }

    @Override
    public void refreshFilter() {
        // Nothing to refresh.
    }

    @Override
    public String toString() {
        return super.toString() + " " + RecipientsFilterType.NATIONS_WITH_NUMBERS.toString();
    }

    @Override
    public String toConfigurationString() {
        return super.toConfigurationString() + "." + RecipientsFilterType.NATIONS_WITH_NUMBERS.name();
    }

}
