package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.listbuilder.RecipientsListBuilder;
import com.github.agadar.telegrammer.core.recipients.listbuilder.RecipientsListBuilderImpl;
import com.github.agadar.telegrammer.core.telegram.history.TelegramHistory;
import com.github.agadar.telegrammer.core.util.StringFunctions;

import java.util.ArrayList;

public class RecipientsListBuilderTranslatorImpl implements RecipientsListBuilderTranslator {

    private final TelegramHistory telegramHistory;
    private final RecipientsFilterTranslator filterTranslator;

    public RecipientsListBuilderTranslatorImpl(TelegramHistory telegramHistory,
            RecipientsFilterTranslator filterTranslator) {
        this.telegramHistory = telegramHistory;
        this.filterTranslator = filterTranslator;
    }

    @Override
    public RecipientsListBuilder toBuilder(String input) {
        var filters = new ArrayList<RecipientsFilter>();

        if (input != null && input.length() > 1) {
            input = input.substring(1, input.length() - 1);
            var split = StringFunctions.stringToArrayList(input);

            split.forEach((part) -> {
                part = part.trim().replace("\"", "");
                filters.add(filterTranslator.toFilter(part));
            });
        }
        return new RecipientsListBuilderImpl(telegramHistory, filters);
    }
}
