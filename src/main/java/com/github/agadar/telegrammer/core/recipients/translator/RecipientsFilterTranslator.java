package com.github.agadar.telegrammer.core.recipients.translator;

import java.util.Collection;

import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

/**
 * Helper class that makes translating between RecipientFilters and
 * corresponding enums/strings easier.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface RecipientsFilterTranslator {

    public RecipientsFilter toFilter(RecipientsFilterType filterType, RecipientsFilterAction filterAction,
            Collection<String> input);

    public RecipientsFilter toFilter(String input);

    public String fromFilter(RecipientsFilter filter);
}
