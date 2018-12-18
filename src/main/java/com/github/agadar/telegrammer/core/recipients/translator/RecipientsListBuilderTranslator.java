package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.telegrammer.core.recipients.listbuilder.RecipientsListBuilder;

/**
 * Helper class that makes translating between IRecipientListBuilders and
 * corresponding enums/strings easier.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface RecipientsListBuilderTranslator {

    public RecipientsListBuilder toBuilder(String input);

    public String fromBuilder(RecipientsListBuilder builder);

}
