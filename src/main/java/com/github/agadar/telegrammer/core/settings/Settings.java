package com.github.agadar.telegrammer.core.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Maintains application settings and access thereof.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
@Slf4j
public class Settings {

    private final Map<String, Setting<?>> settings = new HashMap<>();
    private final String propertiesFileName;

    /**
     * Constructor.
     * 
     * @param propertiesFileName The name of the file to retrieve the properties
     *                           from.
     */
    public Settings(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
    }

    /**
     * Adds a string setting with the given key and value.
     * 
     * @param key   The unique name of the setting.
     * @param value The current value.
     * @return True if the setting is added, else false if a setting with the given
     *         name already exists.
     */
    public boolean addStringSetting(@NonNull String key, @NonNull String value) {
        var setting = new Setting<String>(key, String.class, text -> text, text -> text, value);
        return addSetting(setting);
    }

    /**
     * Adds a boolean setting with the given key and value.
     * 
     * @param key   The unique name of the setting.
     * @param value The current value.
     * @return True if the setting is added, else false if a setting with the given
     *         name already exists.
     */
    public boolean addBooleanSetting(@NonNull String key, boolean value) {
        var setting = new Setting<Boolean>(key, Boolean.class, Boolean::valueOf, bool -> Boolean.toString(bool), value);
        return addSetting(setting);
    }

    /**
     * Adds an integer setting with the given key and value.
     * 
     * @param key   The unique name of the setting.
     * @param value The current value.
     * @return True if the setting is added, else false if a setting with the given
     *         name already exists.
     */
    public boolean addIntegerSetting(@NonNull String key, int value) {
        var setting = new Setting<Integer>(key, Integer.class, Integer::valueOf, integer -> Integer.toString(integer),
                value);
        return addSetting(setting);
    }

    /**
     * Adds a custom setting.
     * 
     * @param setting The custom setting to add.
     * @return True if the setting is added, else false if a setting with the given
     *         name already exists.
     */
    public boolean addSetting(@NonNull Setting<?> setting) {
        if (!settings.containsKey(setting.getKey())) {
            settings.put(setting.getKey(), setting);
            return true;
        }
        log.error("Couldn't add setting '{}': setting with same name already exists", setting.getKey());
        return false;
    }

    /**
     * Sets the value of the setting with the specified key.
     * 
     * @param key   The unique name of the setting to set the value of.
     * @param value The value to set the setting's value to.
     * @return True if success, else false if the setting does not exist or the
     *         value is not of the setting's type.
     */
    public boolean setValue(@NonNull String key, @NonNull Object value) {
        var setting = settings.get(key);
        if (setting != null) {
            return setting.setValue(value);
        }
        log.error("Couldn't set value of setting '{}': setting does not exist", key);
        return false;
    }

    /**
     * Gets the value of the setting with the specified key.
     * 
     * @param      <T> The type of the setting value.
     * @param key  The unique name of the setting.
     * @param type The expected type of the setting value.
     * @return The setting value, else null if the setting does not exist or the
     *         setting value is not of the expected type.
     */
    public <T> T getValue(@NonNull String key, @NonNull Class<T> type) {
        var setting = settings.get(key);
        if (setting == null) {
            log.error("Couldn't get value of setting '{}': setting does not exist", key);
            return null;
        }
        if (!setting.isOfType(type)) {
            log.error("Couldn't get value of setting '{}': setting is not of type '{}'", key, type);
            return null;
        }
        return type.cast(setting.getValue());
    }

    /**
     * Loads the properties file, parsing the values within and updating the
     * settings values.
     */
    public void loadPropertiesFile() {
        var properties = new Properties();

        try (InputStream input = new FileInputStream(propertiesFileName)) {
            properties.load(input);
            loadProperties(properties);
            log.info("Loaded settings from file '{}'", propertiesFileName);

        } catch (FileNotFoundException ex) {
            log.info("Couldn't load properties file '{}': it does not (yet) exist", propertiesFileName);

        } catch (IOException ex) {
            String errorMessage = String.format("An error occured while loading properties from file '%s'",
                    propertiesFileName);
            log.error(errorMessage, ex);
        }
    }

    /**
     * Saves the properties file.
     */
    public void savePropertiesFile() {
        var properties = createProperties();

        try (OutputStream output = new FileOutputStream(this.propertiesFileName)) {
            properties.store(output, null);
            log.info("Saved settings to file '{}'", propertiesFileName);

        } catch (IOException ex) {
            String errorMessage = String.format("An error occured while saving properties to file '%s'",
                    propertiesFileName);
            log.error(errorMessage, ex);
        }
    }

    private void loadProperties(Properties properties) {
        settings.values().forEach(setting -> {
            var value = properties.getProperty(setting.getKey());
            if (value != null) {
                setting.setValueFromString(value);
            }
        });
    }

    private Properties createProperties() {
        var properties = new Properties();
        settings.values().forEach(setting -> {
            properties.setProperty(setting.getKey(), setting.getValueAsString());
        });
        return properties;
    }
}
