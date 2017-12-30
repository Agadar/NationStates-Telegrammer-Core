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

    public ApplicationProperties() {
    }

    public ApplicationProperties(String clientKey, String telegramId, String secretKey, TelegramType lastTelegramType, String fromRegion, boolean runIndefinitely, IRecipientsListBuilder recipientsListBuilder) {
        this.clientKey = clientKey;
        this.telegramId = telegramId;
        this.secretKey = secretKey;
        this.lastTelegramType = lastTelegramType;
        this.fromRegion = fromRegion;
        this.runIndefinitely = runIndefinitely;
        this.recipientsListBuilder = recipientsListBuilder;
    }

}
