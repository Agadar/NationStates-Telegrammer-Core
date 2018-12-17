package com.github.agadar.telegrammer.core.recipients.listbuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.telegrammer.core.recipients.filter.IRecipientsFilter;
import com.github.agadar.telegrammer.core.telegram.history.ITelegramHistory;

import lombok.NonNull;

public class RecipientsListBuilder implements IRecipientsListBuilder {

    private final ITelegramHistory telegramHistory;
    private final List<IRecipientsFilter> filters;

    public RecipientsListBuilder(@NonNull ITelegramHistory telegramHistory) {
        this.telegramHistory = telegramHistory;
        filters = new ArrayList<>();
    }

    public RecipientsListBuilder(@NonNull ITelegramHistory telegramHistory, @NonNull List<IRecipientsFilter> filters) {
        this.telegramHistory = telegramHistory;
        this.filters = filters;
    }

    @Override
    public Set<String> getRecipients() {
        final HashSet<String> recipients = new HashSet<>();
        filters.forEach(filter -> filter.applyFilterToRecipients(recipients));
        telegramHistory.removeOldRecipients(recipients);
        return recipients;
    }

    @Override
    public int addFilter(@NonNull IRecipientsFilter filter) {
        filters.add(filter);
        return filters.indexOf(filter);
    }

    @Override
    public LinkedHashMap<IRecipientsFilter, NationStatesAPIException> refreshFilters() {
        var failedFilters = new LinkedHashMap<IRecipientsFilter, NationStatesAPIException>();
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
    public List<IRecipientsFilter> getFilters() {
        return this.filters;
    }

}
