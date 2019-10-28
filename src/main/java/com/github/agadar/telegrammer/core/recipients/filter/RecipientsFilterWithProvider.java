package com.github.agadar.telegrammer.core.recipients.filter;

import java.util.Collection;
import java.util.Collections;

import com.github.agadar.telegrammer.core.recipients.provider.RecipientsProvider;

import lombok.Getter;
import lombok.NonNull;

/**
 * Implementation of {@link RecipientsFilter} that is backed by an underlying
 * {@link RecipientsProvider}.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class RecipientsFilterWithProvider extends RecipientsFilter {

    @Getter
    private final RecipientsProvider recipientsProvider;

    private Collection<String> filterNationNames = Collections.emptyList();

    /**
     * Constructor.
     * 
     * @param recipientsProvider The underlying provider.
     * @param filterAction       The action this filter should apply.
     */
    public RecipientsFilterWithProvider(@NonNull RecipientsProvider recipientsProvider,
            @NonNull RecipientsFilterAction filterAction) {

        super(filterAction);
        this.recipientsProvider = recipientsProvider;
    }

    @Override
    public void applyFilterToRecipients(@NonNull Collection<String> recipients) {
        switch (filterAction) {
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
        return super.toString() + " " + recipientsProvider.toString();
    }

    @Override
    public String toConfigurationString() {
        return super.toConfigurationString() + "." + recipientsProvider.toConfigurationString();
    }

}
