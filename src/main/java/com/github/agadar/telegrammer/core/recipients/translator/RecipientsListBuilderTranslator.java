package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.telegrammer.core.recipients.filter.IRecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.listbuilder.IRecipientsListBuilder;
import com.github.agadar.telegrammer.core.recipients.listbuilder.RecipientsListBuilder;
import com.github.agadar.telegrammer.core.telegram.history.ITelegramHistory;
import com.github.agadar.telegrammer.core.util.StringFunctions;

import java.util.ArrayList;

public class RecipientsListBuilderTranslator implements IRecipientsListBuilderTranslator {

    private final ITelegramHistory telegramHistory;
    private final IRecipientsFilterTranslator filterTranslator;

    public RecipientsListBuilderTranslator(ITelegramHistory telegramHistory, IRecipientsFilterTranslator filterTranslator) {
        this.telegramHistory = telegramHistory;
        this.filterTranslator = filterTranslator;
    }

    @Override
    public IRecipientsListBuilder toBuilder(String input) {
        final ArrayList<IRecipientsFilter> filters = new ArrayList<>();

        if (input != null && input.length() > 1) {
            input = input.substring(1, input.length() - 1);
            final ArrayList<String> split = StringFunctions.stringToArrayList(input);

            split.forEach((part) -> {
                part = part.replace("\"", "");
                filters.add(filterTranslator.toFilter(part));
            });
        }
        return new RecipientsListBuilder(telegramHistory, filters);
    }

    @Override
    public String fromBuilder(IRecipientsListBuilder builder) {
        final ArrayList<String> stringified = new ArrayList<>();
        builder.getFilters().forEach((filter) -> {
            final String filterString = filterTranslator.fromFilter(filter);
            stringified.add("\"" + filterString + "\"");
        });
        return stringified.toString();
    }

}
