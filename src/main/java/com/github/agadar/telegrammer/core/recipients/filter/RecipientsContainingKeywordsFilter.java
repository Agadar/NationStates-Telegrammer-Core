package com.github.agadar.telegrammer.core.recipients.filter;

import java.util.Collection;

import lombok.NonNull;

/**
 * Implementation of {@link RecipientsFilter} that removes recipients with
 * specified keywords in their name.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class RecipientsContainingKeywordsFilter extends RecipientsFilter {

    private final RecipientsFilterType filterType = RecipientsFilterType.NATIONS_CONTAINING_KEYWORDS;
    private final Collection<String> keywords;

    public RecipientsContainingKeywordsFilter(@NonNull Collection<String> keywords) {
        super(RecipientsFilterAction.REMOVE_FROM_RECIPIENTS);
        this.keywords = keywords;
    }

    @Override
    public void applyFilterToRecipients(Collection<String> recipients) {
        recipients.removeIf(recipient -> recipientContainsAnyKeyword(recipient));
    }

    @Override
    public void refreshFilter() {
        // Nothing to refresh.
    }

    @Override
    public String toString() {
        return super.toString() + " " + filterType.toString() + " " + keywords.toString();
    }

    @Override
    public String toConfigurationString() {
        return super.toConfigurationString() + "." + filterType.name() + keywords.toString();
    }

    private boolean recipientContainsAnyKeyword(String recipient) {
        return keywords.stream().anyMatch(keyword -> recipient.contains(keyword));
    }
}
