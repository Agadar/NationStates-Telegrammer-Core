package com.github.agadar.telegrammer.core.recipients.translator;

import java.util.Collection;
import java.util.HashSet;

import com.github.agadar.telegrammer.core.recipients.filter.NullRecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterWithProvider;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsWithNumbersFilter;
import com.github.agadar.telegrammer.core.util.StringFunctions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecipientsFilterTranslatorImpl implements RecipientsFilterTranslator {

    private final RecipientsProviderTranslator providerTranslator;

    public RecipientsFilterTranslatorImpl(RecipientsProviderTranslator providerTranslator) {
        this.providerTranslator = providerTranslator;
    }

    @Override
    public RecipientsFilter toFilter(RecipientsFilterType filterType, RecipientsFilterAction filterAction,
            Collection<String> input) {

        if (filterType == RecipientsFilterType.NATIONS_WITH_NUMBERS) {
            return new RecipientsWithNumbersFilter();
        }
        var provider = providerTranslator.toProvider(filterType, input);
        return new RecipientsFilterWithProvider(provider, filterAction);
    }

    @Override
    public RecipientsFilter toFilter(String input) {
        if (input == null || input.isEmpty()) {
            return new NullRecipientsFilter();
        }
        String[] split = input.split("\\.");
        RecipientsFilterAction filterAction;
        RecipientsFilterType filterType;

        try {
            filterAction = RecipientsFilterAction.valueOf(split[0]);
            split = split[1].split("\\[");
            filterType = RecipientsFilterType.valueOf(split[0]);
        } catch (Exception ex) {
            log.error("Failed to parse input", ex);
            return new NullRecipientsFilter();
        }
        Collection<String> params;

        if (split.length > 1) {
            split[1] = split[1].substring(0, split[1].length() - 1);
            params = StringFunctions.stringToHashSet(split[1]);
        } else {
            params = new HashSet<>();
        }
        return toFilter(filterType, filterAction, params);
    }

    @Override
    public String fromFilter(RecipientsFilter filter) {
        if (filter == null || filter.getFilterAction() == null || filter instanceof NullRecipientsFilter) {
            return "";
        }
        String stringified = filter.getFilterAction().name();

        if (filter instanceof RecipientsFilterWithProvider) {
            var recipientsFilter = (RecipientsFilterWithProvider) filter;
            stringified += "." + providerTranslator.fromProvider(recipientsFilter.getRecipientsProvider());

        } else if (filter instanceof RecipientsWithNumbersFilter) {
            stringified += "." + RecipientsFilterType.NATIONS_WITH_NUMBERS.name();

        }
        return stringified;
    }

}
