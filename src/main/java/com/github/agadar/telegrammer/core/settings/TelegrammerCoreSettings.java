package com.github.agadar.telegrammer.core.settings;

import static com.github.agadar.telegrammer.core.settings.TelegrammerCoreSettingsKeys.*;

import com.github.agadar.telegrammer.core.misc.TelegramType;
import com.github.agadar.telegrammer.core.recipients.listbuilder.RecipientsListBuilder;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsListBuilderTranslator;

import lombok.NonNull;

/**
 * Application settings specific to the telegrammer core library, wrapping
 * around a {@link Settings} instance.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class TelegrammerCoreSettings {

    private final Settings settings;

    /**
     * Constructor.
     * 
     * @param settings          For registering and getting/setting telegrammer
     *                          settings.
     * @param builderTranslator Specifically for parsing the 'filters' setting.
     */
    public TelegrammerCoreSettings(@NonNull Settings settings, @NonNull RecipientsListBuilderTranslator builderTranslator) {
        this.settings = settings;
        settings.addStringSetting(CLIENT_KEY.getKey(), "");
        settings.addStringSetting(FROM_REGION.getKey(), "");
        settings.addStringSetting(SECRET_KEY.getKey(), "");
        settings.addStringSetting(TELEGRAM_ID.getKey(), "");
        settings.addBooleanSetting(RUN_INDEFINITELY.getKey(), false);
        settings.addBooleanSetting(UPDATE_AFTER_EVERY_TELEGRAM.getKey(), false);
        settings.addSetting(createTelegramTypeSetting());
        settings.addSetting(createFiltersSetting(builderTranslator));
    }

    public void setClientKey(@NonNull String clientKey) {
        settings.setValue(CLIENT_KEY.getKey(), clientKey);
    }

    public String getClientKey() {
        return settings.getValue(CLIENT_KEY.getKey(), String.class);
    }

    public void setFromRegion(@NonNull String fromRegion) {
        settings.setValue(FROM_REGION.getKey(), fromRegion);
    }

    public String getFromRegion() {
        return settings.getValue(FROM_REGION.getKey(), String.class);
    }

    public void setSecretKey(@NonNull String secretKey) {
        settings.setValue(SECRET_KEY.getKey(), secretKey);
    }

    public String getSecretKey() {
        return settings.getValue(SECRET_KEY.getKey(), String.class);
    }

    public void setTelegramId(@NonNull String telegramId) {
        settings.setValue(TELEGRAM_ID.getKey(), telegramId);
    }

    public String getTelegramId() {
        return settings.getValue(TELEGRAM_ID.getKey(), String.class);
    }

    public void setRunIndefinitely(boolean runIndefinitely) {
        settings.setValue(RUN_INDEFINITELY.getKey(), runIndefinitely);
    }

    public boolean getRunIndefinitely() {
        return settings.getValue(RUN_INDEFINITELY.getKey(), boolean.class);
    }

    public void setUpdateAfterEveryTelegram(boolean updateAfterEveryTelegram) {
        settings.setValue(UPDATE_AFTER_EVERY_TELEGRAM.getKey(), updateAfterEveryTelegram);
    }

    public boolean getUpdateAfterEveryTelegram() {
        return settings.getValue(UPDATE_AFTER_EVERY_TELEGRAM.getKey(), boolean.class);
    }

    public void setTelegramType(@NonNull TelegramType telegramType) {
        settings.setValue(TELEGRAM_TYPE.getKey(), TelegramType.class);
    }

    public TelegramType getTelegramType() {
        return settings.getValue(TELEGRAM_TYPE.getKey(), TelegramType.class);
    }

    public void setFilters(@NonNull RecipientsListBuilder filters) {
        settings.setValue(FILTERS.getKey(), filters);
    }

    public RecipientsListBuilder getFilters() {
        return settings.getValue(FILTERS.getKey(), RecipientsListBuilder.class);
    }

    private Setting<TelegramType> createTelegramTypeSetting() {
        return new Setting<TelegramType>(TELEGRAM_TYPE.getKey(), TelegramType.class,
                text -> Enum.valueOf(TelegramType.class, text), telegramType -> telegramType.name(),
                TelegramType.NORMAL);
    }

    private Setting<RecipientsListBuilder> createFiltersSetting(RecipientsListBuilderTranslator builderTranslator) {
        return new Setting<RecipientsListBuilder>(FILTERS.getKey(), RecipientsListBuilder.class,
                text -> builderTranslator.toBuilder(text), builder -> builder.toConfigurationString(),
                builderTranslator.toBuilder(""));
    }

}
