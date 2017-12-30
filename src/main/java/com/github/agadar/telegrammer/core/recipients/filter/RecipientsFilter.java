package com.github.agadar.telegrammer.core.recipients.filter;

import com.github.agadar.telegrammer.core.recipients.provider.IRecipientsProvider;

import java.util.HashSet;
import java.util.Set;

public class RecipientsFilter implements IRecipientsFilter {

    public final IRecipientsProvider recipientsProvider;
    public final RecipientsFilterType filterType;

    private Set<String> filterNationNames = new HashSet<>();

    public RecipientsFilter(IRecipientsProvider recipientsProvider, RecipientsFilterType filterType) {
        this.recipientsProvider = recipientsProvider;
        this.filterType = filterType;
    }

    @Override
    public void applyFilterToRecipients(Set<String> recipients) {
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
