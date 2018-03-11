package com.github.agadar.telegrammer.core.properties;

import com.github.agadar.telegrammer.core.recipients.listbuilder.IRecipientsListBuilder;
import com.github.agadar.telegrammer.core.telegram.TelegramType;

/**
 * Holds properties used and set throughout the application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class ApplicationProperties {

    public String clientKey;
    public String telegramId;
    public String secretKey;
    public TelegramType lastTelegramType;
    public String fromRegion;
    public boolean runIndefinitely;
    public IRecipientsListBuilder recipientsListBuilder;
    public boolean updateRecipientsAfterEveryTelegram;
}
