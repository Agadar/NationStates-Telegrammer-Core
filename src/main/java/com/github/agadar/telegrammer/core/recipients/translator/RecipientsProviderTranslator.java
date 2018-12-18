package com.github.agadar.telegrammer.core.recipients.translator;

import java.util.Collection;

import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.recipients.provider.RecipientsProvider;

/**
 * Helper class that makes translating between IRecipientProviders and
 * corresponding enums/strings easier.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface RecipientsProviderTranslator {

    public RecipientsProvider toProvider(RecipientsProviderType providerType, Collection<String> input);

    public RecipientsProvider toProvider(String input);

    public String fromProvider(RecipientsProvider provider);
}
