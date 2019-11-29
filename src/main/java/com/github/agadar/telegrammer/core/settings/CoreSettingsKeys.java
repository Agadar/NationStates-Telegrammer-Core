package com.github.agadar.telegrammer.core.settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The keys of the telegrammer core specific settings.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
@RequiredArgsConstructor
@Getter
public enum CoreSettingsKeys {

    CLIENT_KEY("clientKey"),
    FROM_REGION("fromRegion"),
    SECRET_KEY("secretKey"),
    TELEGRAM_ID("telegramId"),
    RUN_INDEFINITELY("runIndefinitely"),
    UPDATE_AFTER_EVERY_TELEGRAM("updateRecipientsAfterEveryTelegram"),
    TELEGRAM_TYPE("telegramType"),
    FILTERS("filters");

    private final String key;
}
