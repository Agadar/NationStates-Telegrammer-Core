package com.github.agadar.telegrammer.core.properties;

import com.github.agadar.telegrammer.core.recipients.listbuilder.RecipientsListBuilder;
import com.github.agadar.telegrammer.core.telegram.TelegramType;

import lombok.Getter;
import lombok.Setter;

/**
 * Holds properties used and set throughout the application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Getter
@Setter
public class ApplicationProperties {

    private String clientKey;
    private String telegramId;
    private String secretKey;
    private TelegramType lastTelegramType;
    private String fromRegion;
    private boolean runIndefinitely;
    private RecipientsListBuilder recipientsListBuilder;
    private boolean updateRecipientsAfterEveryTelegram;
}
