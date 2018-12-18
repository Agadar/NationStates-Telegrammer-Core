package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

import java.util.HashSet;

/**
 * Helper class that makes translating between IRecipientFilters and
 * corresponding enums/strings easier.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface RecipientsFilterTranslator {

    public RecipientsFilter toFilter(RecipientsFilterType filterType, RecipientsProviderType providerType, HashSet<String> input);

    public RecipientsFilter toFilter(String input);

    public String fromFilter(RecipientsFilter filter);
}
