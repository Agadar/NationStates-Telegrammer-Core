package com.github.agadar.telegrammer.core.recipientslistbuilder;

import com.github.agadar.telegrammer.core.recipientsfilter.IRecipientsFilter;
import com.github.agadar.telegrammer.core.telegramhistory.ITelegramHistory;

import java.util.ArrayList;
import java.util.HashSet;

public class RecipientsListBuilder implements IRecipientsListBuilder {
    
    private final ITelegramHistory telegramHistory;
    private final ArrayList<IRecipientsFilter> filters = new ArrayList<>();
    
    public RecipientsListBuilder(ITelegramHistory telegramHistory) {
        this.telegramHistory = telegramHistory;
    }
    
    @Override
    public HashSet<String> getRecipients() {
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
    
}
