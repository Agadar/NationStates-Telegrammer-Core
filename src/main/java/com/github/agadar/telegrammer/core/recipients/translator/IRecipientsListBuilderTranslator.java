package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.telegrammer.core.recipients.listbuilder.IRecipientsListBuilder;

/**
 * Helper class that makes translating between IRecipientListBuilders and
 * corresponding enums/strings easier.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface IRecipientsListBuilderTranslator {

    public IRecipientsListBuilder toBuilder(String input);

    public String fromBuilder(IRecipientsListBuilder builder);

}
