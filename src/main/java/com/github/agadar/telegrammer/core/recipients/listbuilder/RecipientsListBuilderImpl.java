package com.github.agadar.telegrammer.core.recipients.listbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.telegram.history.TelegramHistory;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecipientsListBuilderImpl implements RecipientsListBuilder {

    private final TelegramHistory telegramHistory;
    private final List<RecipientsFilter> filters;

    public RecipientsListBuilderImpl(@NonNull TelegramHistory telegramHistory) {
        this.telegramHistory = telegramHistory;
        filters = new ArrayList<>();
    }

    public RecipientsListBuilderImpl(@NonNull TelegramHistory telegramHistory,
            @NonNull List<RecipientsFilter> filters) {
        this.telegramHistory = telegramHistory;
        this.filters = filters;
    }

    @Override
    public Collection<String> getRecipients(@NonNull String telegramId) {
        var recipients = new LinkedHashSet<String>();
        filters.forEach(filter -> filter.applyFilterToRecipients(recipients));
        telegramHistory.removeOldRecipients(recipients, telegramId);
        return recipients;
    }

    @Override
    public int addFilter(@NonNull RecipientsFilter filter) {
        filters.add(filter);
        return filters.indexOf(filter);
    }

    @Override
    public Map<RecipientsFilter, NationStatesAPIException> refreshFilters() {
        var failedFilters = new LinkedHashMap<RecipientsFilter, NationStatesAPIException>();
        filters.forEach(filter -> {
            try {
                filter.refreshFilter();
            } catch (NationStatesAPIException ex) {
                log.error("An error occured while refreshing a filter", ex);
                failedFilters.put(filter, ex);
            }
        });
        return failedFilters;
    }

    @Override
    public void removeFilterAt(int index) {
        filters.remove(index);
    }

    @Override
    public void resetFilters() {
        filters.clear();
    }

    @Override
    public List<RecipientsFilter> getFilters() {
        return this.filters;
    }

    @Override
    public String toConfigurationString() {
        return filters.stream()
                .map(filter -> "\"" + filter.toConfigurationString() + "\"")
                .collect(Collectors.joining(", ", "[", "]"));
    }

}
