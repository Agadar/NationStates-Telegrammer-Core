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

    protected final String defaultStringValue = "";
    protected final String defaultBooleanValue = "false";

    private final String propertiesFileName = ".nationstates-telegrammer.properties";
    private final IRecipientsListBuilderTranslator builderTranslator;

    public PropertiesManager(IRecipientsListBuilderTranslator builderTranslator) {
	this.builderTranslator = builderTranslator;
    }

    @Override
    public boolean saveProperties(ApplicationProperties properties) {
	if (properties == null) {
	    return false;
	}

	final Properties propertiesMap = new Properties();
	this.setPropertiesFromApplicationProperties(propertiesMap, properties);

	try (OutputStream output = new FileOutputStream(propertiesFileName)) {
	    propertiesMap.store(output, null);
	} catch (IOException io) {
	    return false;
	}
	return true;
    }

    @Override
    public ApplicationProperties loadProperties(ApplicationProperties properties) {
	if (properties == null) {
	    properties = this.createApplicationProperties();
	}

	final Properties propertiesMap = new Properties();

	try (InputStream input = new FileInputStream(propertiesFileName);) {
	    propertiesMap.load(input);
	} catch (IOException ex) {
	    // Ignore: we're just going to use default values instead.
	}

	this.setApplicationPropertiesFromProperties(properties, propertiesMap);
	return properties;
    }

    /**
     * Instantiates a new ApplicationProperties instance.
     * 
     * @return
     */
    protected ApplicationProperties createApplicationProperties() {
	return new ApplicationProperties();
    }

    /**
     * Fills the values of an ApplicationProperties with the contents of a
     * Properties, using default values for missing entries.
     * 
     * @param target
     * @param source
     */
    protected void setApplicationPropertiesFromProperties(ApplicationProperties target, Properties source) {
	target.clientKey = source.getProperty("clientKey", defaultStringValue);
	target.fromRegion = source.getProperty("fromRegion", defaultStringValue);
	target.lastTelegramType = valueOf(TelegramType.class, source.getProperty("telegramType"), TelegramType.NORMAL);
	target.recipientsListBuilder = builderTranslator.toBuilder(source.getProperty("filters"));
	target.runIndefinitely = Boolean.valueOf(source.getProperty("runIndefinitely", defaultBooleanValue));
	target.secretKey = source.getProperty("secretKey", defaultStringValue);
	target.telegramId = source.getProperty("telegramId", defaultStringValue);
    }

    /**
     * Fills the values of a Properties with the contents of a
     * ApplicationProperties, using default values for missing entries.
     * 
     * @param target
     * @param source
     */
    protected void setPropertiesFromApplicationProperties(Properties target, ApplicationProperties source) {
	target.setProperty("clientKey", source.clientKey == null ? defaultStringValue : source.clientKey);
	target.setProperty("telegramId", source.telegramId == null ? defaultStringValue : source.telegramId);
	target.setProperty("secretKey", source.secretKey == null ? defaultStringValue : source.secretKey);
	target.setProperty("telegramType",
		source.lastTelegramType != null ? source.lastTelegramType.name() : TelegramType.NORMAL.name());
	target.setProperty("fromRegion", source.fromRegion == null ? defaultStringValue : source.fromRegion);
	target.setProperty("runIndefinitely", Boolean.toString(source.runIndefinitely));
	target.setProperty("filters", builderTranslator.fromBuilder(source.recipientsListBuilder));
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
    private <T extends Enum<T>> T valueOf(Class<T> type, String string, T defaultValue) {
	try {
	    return Enum.valueOf(type, string);
	} catch (IllegalArgumentException | NullPointerException ex) {
	    return defaultValue;
	}
    }
}
