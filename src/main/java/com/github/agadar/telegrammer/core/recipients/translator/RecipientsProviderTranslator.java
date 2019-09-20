package com.github.agadar.telegrammer.core.recipients.translator;

import java.util.Collection;

import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.recipients.provider.RecipientsProvider;

/**
 * Helper class that makes translating between IRecipientProviders and
 * corresponding enums/strings easier.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface RecipientsProviderTranslator {

    public RecipientsProvider toProvider(RecipientsFilterType filterType, Collection<String> input);
}
