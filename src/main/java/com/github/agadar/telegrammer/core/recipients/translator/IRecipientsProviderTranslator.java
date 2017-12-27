package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.recipients.provider.IRecipientsProvider;

import java.util.HashSet;

/**
 * Helper class that makes translating between IRecipientProviders and
 * corresponding enums easier.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface IRecipientsProviderTranslator {

    public IRecipientsProvider toProvider(RecipientsProviderType providerType, HashSet<String> input);
}
