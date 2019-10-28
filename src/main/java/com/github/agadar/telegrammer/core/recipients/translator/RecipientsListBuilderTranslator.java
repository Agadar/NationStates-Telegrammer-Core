package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.telegrammer.core.recipients.listbuilder.RecipientsListBuilder;

/**
 * Helper class that makes translating between RecipientListBuilders and
 * corresponding enums/strings easier.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface RecipientsListBuilderTranslator {

    public RecipientsListBuilder toBuilder(String input);

}
