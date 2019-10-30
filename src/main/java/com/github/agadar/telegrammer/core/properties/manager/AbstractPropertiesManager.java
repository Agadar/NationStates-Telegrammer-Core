package com.github.agadar.telegrammer.core.properties.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.github.agadar.telegrammer.core.properties.ApplicationProperties;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsListBuilderTranslator;
import com.github.agadar.telegrammer.core.telegram.TelegramType;

import lombok.extern.slf4j.Slf4j;

/**
 * Partial implementation of IPropertiesManager, containing the bare minimum
 * functionalities for properly handling ApplicationProperties.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 * @param <T>
 */
@Slf4j
public abstract class AbstractPropertiesManager<T extends ApplicationProperties> implements PropertiesManager<T> {

    protected final String defaultStringValue = "";
    protected final String defaultBooleanValue = "false";

    private String propertiesFileName;
    private T properties;

    private final RecipientsListBuilderTranslator builderTranslator;

    public AbstractPropertiesManager(RecipientsListBuilderTranslator builderTranslator, String propertiesFileName) {
        this.builderTranslator = builderTranslator;
        this.propertiesFileName = propertiesFileName;
    }

    @Override
    public boolean persistPropertiesToFileSystem() {
        if (properties == null) {
            return false;
        }

        var propertiesMap = new Properties();
        this.setPropertiesFromApplicationProperties(propertiesMap, properties);

        try (OutputStream output = new FileOutputStream(this.propertiesFileName)) {
            propertiesMap.store(output, null);

        } catch (IOException ex) {
            log.error("An error occured while persisting properties to file", ex);
            return false;
        }
        return true;
    }

    @Override
    public T loadPropertiesFromFileSystem() {
        if (properties == null) {
            properties = this.createApplicationProperties();
        }

        var propertiesMap = new Properties();

        try (InputStream input = new FileInputStream(this.propertiesFileName);) {
            propertiesMap.load(input);

        } catch (FileNotFoundException ex) {
            log.info("Couldn't read properties file as it does not (yet) exist");

        } catch (IOException ex) {
            log.error("An error occured while loading properties from file", ex);
        }

        this.setApplicationPropertiesFromProperties(properties, propertiesMap);
        return properties;
    }

    @Override
    public T getProperties() {
        return properties;
    }

    /**
     * Instantiates a new ApplicationProperties instance.
     * 
     * @return
     */
    protected abstract T createApplicationProperties();

    /**
     * Fills the values of an ApplicationProperties with the contents of a
     * Properties, using default values for missing entries.
     * 
     * @param target
     * @param source
     */
    protected void setApplicationPropertiesFromProperties(T target, Properties source) {
        target.setClientKey(source.getProperty("clientKey", defaultStringValue));
        target.setFromRegion(source.getProperty("fromRegion", defaultStringValue));
        target.setLastTelegramType(
                valueOf(TelegramType.class, source.getProperty("telegramType"), TelegramType.NORMAL));
        target.setRecipientsListBuilder(builderTranslator.toBuilder(source.getProperty("filters")));
        target.setRunIndefinitely(Boolean.valueOf(source.getProperty("runIndefinitely", defaultBooleanValue)));
        target.setSecretKey(source.getProperty("secretKey", defaultStringValue));
        target.setTelegramId(source.getProperty("telegramId", defaultStringValue));
        target.setUpdateRecipientsAfterEveryTelegram(
                Boolean.valueOf(source.getProperty("updateRecipientsAfterEveryTelegram", defaultBooleanValue)));
    }

    /**
     * Fills the values of a Properties with the contents of a
     * ApplicationProperties, using default values for missing entries.
     * 
     * @param target
     * @param source
     */
    protected void setPropertiesFromApplicationProperties(Properties target, T source) {
        target.setProperty("clientKey", source.getClientKey() == null ? defaultStringValue : source.getClientKey());
        target.setProperty("telegramId", source.getTelegramId() == null ? defaultStringValue : source.getTelegramId());
        target.setProperty("secretKey", source.getSecretKey() == null ? defaultStringValue : source.getSecretKey());
        target.setProperty("telegramType", source.getLastTelegramType() != null ? source.getLastTelegramType().name()
                : TelegramType.NORMAL.name());
        target.setProperty("fromRegion", source.getFromRegion() == null ? defaultStringValue : source.getFromRegion());
        target.setProperty("runIndefinitely", Boolean.toString(source.isRunIndefinitely()));
        target.setProperty("filters", source.getRecipientsListBuilder().toConfigurationString());
        target.setProperty("updateRecipientsAfterEveryTelegram",
                Boolean.toString(source.isUpdateRecipientsAfterEveryTelegram()));
    }

    /**
     * Calls Enum.valueOf(...) only instead of throwing an IllegalArgumentException
     * if the specified enum type has no constant with the specified name, it
     * returns the specified default value.
     *
     * @param <T>
     * @param type
     * @param string
     * @param defaultValue
     * @return
     */
    private <V extends Enum<V>> V valueOf(Class<V> type, String string, V defaultValue) {
        try {
            if (string == null) {
                return defaultValue;
            }
            return Enum.valueOf(type, string);

        } catch (IllegalArgumentException ex) {
            log.error("An error occured while parsing a value from the properties file", ex);
            return defaultValue;
        }
    }
}
