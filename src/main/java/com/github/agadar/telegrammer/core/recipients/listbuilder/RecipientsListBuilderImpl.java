package com.github.agadar.telegrammer.core.recipients.listbuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.telegram.history.TelegramHistory;

import lombok.NonNull;

public class RecipientsListBuilderImpl implements RecipientsListBuilder {

    private final TelegramHistory telegramHistory;
    private final List<RecipientsFilter> filters;

    public RecipientsListBuilderImpl(@NonNull TelegramHistory telegramHistory) {
        this.telegramHistory = telegramHistory;
        filters = new ArrayList<>();
    }

    public RecipientsListBuilderImpl(@NonNull TelegramHistory telegramHistory, @NonNull List<RecipientsFilter> filters) {
        this.telegramHistory = telegramHistory;
        this.filters = filters;
    }

    @Override
    public Collection<String> getRecipients() {
        var recipients = new LinkedHashSet<String>();
        filters.forEach(filter -> filter.applyFilterToRecipients(recipients));
        telegramHistory.removeOldRecipients(recipients);
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

}
