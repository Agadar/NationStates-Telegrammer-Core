package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.NullRecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterImpl;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.recipients.provider.RecipientsProvider;

import java.util.HashSet;

public class RecipientsFilterTranslatorImpl implements RecipientsFilterTranslator {

    private final RecipientsProviderTranslator providerTranslator;

    public RecipientsFilterTranslatorImpl(RecipientsProviderTranslator providerTranslator) {
        this.providerTranslator = providerTranslator;
    }

    @Override
    public RecipientsFilter toFilter(RecipientsFilterType filterType, RecipientsProviderType providerType, HashSet<String> input) {
        final RecipientsProvider provider = providerTranslator.toProvider(providerType, input);
        return new RecipientsFilterImpl(provider, filterType);
    }

    @Override
    public RecipientsFilter toFilter(String input) {
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
        final RecipientsProvider provider = providerTranslator.toProvider(providerString);
        return new RecipientsFilterImpl(provider, filterType);
    }

    @Override
    public String fromFilter(RecipientsFilter filter) {
        if (filter instanceof RecipientsFilterImpl) {
            final RecipientsFilterImpl recipientsFilter = (RecipientsFilterImpl) filter;
            String stringified = recipientsFilter.filterType.name();
            stringified += "." + providerTranslator.fromProvider(recipientsFilter.recipientsProvider);
            return stringified;
        }
        return "";
    }

}
