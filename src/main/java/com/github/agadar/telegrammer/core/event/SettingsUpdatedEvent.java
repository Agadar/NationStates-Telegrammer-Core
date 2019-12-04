package com.github.agadar.telegrammer.core.event;

import java.util.EventObject;
import java.util.List;

import com.github.agadar.telegrammer.core.Telegrammer;
import com.github.agadar.telegrammer.core.misc.TelegramType;

import lombok.Builder;
import lombok.Getter;

/**
 * Event fired by {@link Telegrammer} when the settings have been updated.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
@Getter
public class SettingsUpdatedEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    private final String clientKey;
    private final String fromRegion;
    private final String secretKey;
    private final String telegramId;
    private final boolean runIndefinitely;
    private final boolean updateAfterEveryTelegram;
    private final TelegramType telegramType;
    private final List<String> filters;
    private final int numberOfRecipients;

    @Builder
    public SettingsUpdatedEvent(Object source, String clientKey, String fromRegion, String secretKey, String telegramId,
            boolean runIndefinitely, boolean updateAfterEveryTelegram, TelegramType telegramType,
            List<String> filters, int numberOfRecipients) {
        super(source);
        this.clientKey = clientKey;
        this.fromRegion = fromRegion;
        this.secretKey = secretKey;
        this.telegramId = telegramId;
        this.runIndefinitely = runIndefinitely;
        this.updateAfterEveryTelegram = updateAfterEveryTelegram;
        this.telegramType = telegramType;
        this.filters = filters;
        this.numberOfRecipients = numberOfRecipients;
    }
}
