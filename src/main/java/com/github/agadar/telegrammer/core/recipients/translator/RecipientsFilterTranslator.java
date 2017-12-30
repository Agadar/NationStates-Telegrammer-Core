package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.recipients.filter.IRecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.NullRecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.recipients.provider.IRecipientsProvider;

import java.util.HashSet;

public class RecipientsFilterTranslator implements IRecipientsFilterTranslator {

    private final IRecipientsProviderTranslator providerTranslator;

    public RecipientsFilterTranslator(IRecipientsProviderTranslator providerTranslator) {
        this.providerTranslator = providerTranslator;
    }

    @Override
    public IRecipientsFilter toFilter(RecipientsFilterType filterType, RecipientsProviderType providerType, HashSet<String> input) {
        final IRecipientsProvider provider = providerTranslator.toProvider(providerType, input);
        return new RecipientsFilter(provider, filterType);
    }

    @Override
    public IRecipientsFilter toFilter(String input) {
        if (input == null || input.isEmpty()) {
            return new NullRecipientsFilter();
        }
        final String[] split = input.split("\\.");
        RecipientsFilterType filterType;

        try {
            filterType = RecipientsFilterType.valueOf(split[0]);
        } catch (IllegalArgumentException e) {
            return new NullRecipientsFilter();
        }
        final String providerString = split.length > 1 ? split[1] : "";
        final IRecipientsProvider provider = providerTranslator.toProvider(providerString);
        return new RecipientsFilter(provider, filterType);
    }

    @Override
    public String fromFilter(IRecipientsFilter filter) {
        if (filter instanceof RecipientsFilter) {
            final RecipientsFilter recipientsFilter = (RecipientsFilter) filter;
            String stringified = recipientsFilter.filterType.name();
            stringified += "." + providerTranslator.fromProvider(recipientsFilter.recipientsProvider);
            return stringified;
        }
        return "";
    }

}
