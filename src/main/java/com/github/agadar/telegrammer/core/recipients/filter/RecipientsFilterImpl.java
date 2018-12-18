package com.github.agadar.telegrammer.core.recipients.filter;

import java.util.Collection;
import java.util.Collections;

import com.github.agadar.telegrammer.core.recipients.provider.RecipientsProvider;

import lombok.NonNull;

public class RecipientsFilterImpl implements RecipientsFilter {

    public final RecipientsProvider recipientsProvider;
    public final RecipientsFilterType filterType;

    private Collection<String> filterNationNames = Collections.emptyList();

    public RecipientsFilterImpl(@NonNull RecipientsProvider recipientsProvider, @NonNull RecipientsFilterType filterType) {
        this.recipientsProvider = recipientsProvider;
        this.filterType = filterType;
    }

    @Override
    public void applyFilterToRecipients(@NonNull Collection<String> recipients) {
        switch (filterType) {
        case ADD_TO_RECIPIENTS:
            recipients.addAll(filterNationNames);
            break;
        case REMOVE_FROM_RECIPIENTS:
            recipients.removeAll(filterNationNames);
            break;
        case REMOVE_RECIPIENTS_NOT_IN:
            recipients.retainAll(filterNationNames);
            break;
        }
    }

    @Override
    public void refreshFilter() {
        filterNationNames = recipientsProvider.getRecipients();
    }

    @Override
    public String toString() {
        String stringified = "";

        switch (filterType) {
        case ADD_TO_RECIPIENTS:
            stringified += "(+)";
            break;
        case REMOVE_FROM_RECIPIENTS:
            stringified += "(-)";
            break;
        case REMOVE_RECIPIENTS_NOT_IN:
            stringified += "(!)";
            break;
        }
        return stringified + " " + recipientsProvider.toString();
    }

}
