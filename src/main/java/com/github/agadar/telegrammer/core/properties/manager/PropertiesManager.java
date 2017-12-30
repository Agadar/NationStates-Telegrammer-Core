package com.github.agadar.telegrammer.core.properties.manager;

import com.github.agadar.telegrammer.core.properties.ApplicationProperties;
import com.github.agadar.telegrammer.core.recipients.translator.IRecipientsListBuilderTranslator;
import com.github.agadar.telegrammer.core.telegram.TelegramType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertiesManager implements IPropertiesManager {
    
    private final String propertiesFileName = ".nationstates-telegrammer.properties";
    private final String defaultStringValue = "";
    private final String defaultBooleanValue = "false";
    private final IRecipientsListBuilderTranslator builderTranslator;
    
    public PropertiesManager(IRecipientsListBuilderTranslator builderTranslator) {
        this.builderTranslator = builderTranslator;
    }
    
    @Override
    public boolean saveProperties(ApplicationProperties properties) {
        if (properties == null) {
            return false;
        }

        // Prepare properties object.
        final Properties propertiesMap = new Properties();
        propertiesMap.setProperty("clientKey", properties.clientKey == null ? defaultStringValue : properties.clientKey);
        propertiesMap.setProperty("telegramId", properties.telegramId == null ? defaultStringValue : properties.telegramId);
        propertiesMap.setProperty("secretKey", properties.secretKey == null ? defaultStringValue : properties.secretKey);
        propertiesMap.setProperty("telegramType", properties.lastTelegramType != null ? properties.lastTelegramType.name() : TelegramType.NORMAL.name());
        propertiesMap.setProperty("fromRegion", properties.fromRegion == null ? defaultStringValue : properties.fromRegion);
        propertiesMap.setProperty("runIndefinitely", Boolean.toString(properties.runIndefinitely));
        propertiesMap.setProperty("filters", builderTranslator.fromBuilder(properties.recipientsListBuilder));

        // Save to file.
        try (OutputStream output = new FileOutputStream(propertiesFileName)) {
            propertiesMap.store(output, null);
        } catch (IOException io) {
            return false;
        }
        return true;
    }
    
    @Override
    public ApplicationProperties loadProperties() {
        final Properties props = new Properties();
        
        try (InputStream input = new FileInputStream(propertiesFileName);) {
            props.load(input);
        } catch (IOException ex) {
            return new ApplicationProperties(
                    defaultStringValue,
                    defaultStringValue,
                    defaultStringValue,
                    TelegramType.NORMAL,
                    defaultStringValue,
                    Boolean.valueOf(defaultBooleanValue),
                    builderTranslator.toBuilder(null));
        }
        
        return new ApplicationProperties(
                props.getProperty("clientKey", defaultStringValue),
                props.getProperty("telegramId", defaultStringValue),
                props.getProperty("secretKey", defaultStringValue),
                valueOf(TelegramType.class, props.getProperty("telegramType"), TelegramType.NORMAL),
                props.getProperty("fromRegion", defaultStringValue),
                Boolean.valueOf(props.getProperty("runIndefinitely", defaultBooleanValue)),
                builderTranslator.toBuilder(props.getProperty("filters")));
    }

    /**
     * Calls Enum.valueOf(...) only instead of throwing an
     * IllegalArgumentException if the specified enum type has no constant with
     * the specified name, it returns the specified default value.
     *
     * @param <T>
     * @param type
     * @param string
     * @param defaultValue
     * @return
     */
    private <T extends Enum<T>> T valueOf(Class<T> type, String string, T defaultValue) {
        try {
            return Enum.valueOf(type, string);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return defaultValue;
        }
    }
}
