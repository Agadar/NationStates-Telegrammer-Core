package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.recipients.filter.IRecipientsFilter;
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

}
