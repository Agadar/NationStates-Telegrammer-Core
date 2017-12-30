package com.github.agadar.telegrammer.core.recipients.listbuilder;

import com.github.agadar.telegrammer.core.recipients.filter.IRecipientsFilter;
import com.github.agadar.telegrammer.core.telegram.history.ITelegramHistory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipientsListBuilder implements IRecipientsListBuilder {

    private final ITelegramHistory telegramHistory;
    private final List<IRecipientsFilter> filters;

    public RecipientsListBuilder(ITelegramHistory telegramHistory) {
        this.telegramHistory = telegramHistory;
        filters = new ArrayList<>();
    }

    public RecipientsListBuilder(ITelegramHistory telegramHistory, List<IRecipientsFilter> filters) {
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
    public int addFilter(IRecipientsFilter filter) {
        filters.add(filter);
        return filters.indexOf(filter);
    }

    @Override
    public void refreshFilters() {
        filters.forEach(filter -> filter.refreshFilter());
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
