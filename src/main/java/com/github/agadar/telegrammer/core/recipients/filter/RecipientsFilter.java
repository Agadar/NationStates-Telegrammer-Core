package com.github.agadar.telegrammer.core.recipients.filter;

import com.github.agadar.telegrammer.core.recipients.provider.IRecipientsProvider;

import java.util.Collection;
import java.util.HashSet;

public class RecipientsFilter implements IRecipientsFilter {

    private final IRecipientsProvider recipientsProvider;
    private final RecipientsFilterType filterType;

    private HashSet<String> filterNationNames = new HashSet<>();

    public RecipientsFilter(IRecipientsProvider recipientsProvider, RecipientsFilterType filterType) {
        this.recipientsProvider = recipientsProvider;
        this.filterType = filterType;
    }

    @Override
    public void applyFilterToRecipients(Collection<String> recipients) {
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
