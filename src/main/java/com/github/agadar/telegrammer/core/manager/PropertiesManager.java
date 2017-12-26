package com.github.agadar.telegrammer.core.manager;

import com.github.agadar.telegrammer.core.enums.TelegramType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertiesManager implements IPropertiesManager {

    // Standard name for this app's properties file.
    private final String FILENAME = ".nationstates-telegrammer.properties";
    private final String DEFAULT_STRING_VAL = "";
    private final String DEFAULT_BOOL_VAL = "false";

    // Variables loaded from and saved to the properties file.
    private String clientKey;
    private String telegramId;
    private String secretKey;
    private TelegramType lastTelegramType;
    private String fromRegion;
    private boolean dryRun;

    @Override
    public boolean saveProperties() {
        // Prepare properties object.
        final Properties props = new Properties();
        props.setProperty("clientKey", clientKey == null ? DEFAULT_STRING_VAL : clientKey);
        props.setProperty("telegramId", telegramId == null ? DEFAULT_STRING_VAL : telegramId);
        props.setProperty("secretKey", secretKey == null ? DEFAULT_STRING_VAL : secretKey);
        props.setProperty("telegramType", lastTelegramType != null ? lastTelegramType.name() : TelegramType.NORMAL.name());
        props.setProperty("fromRegion", fromRegion == null ? DEFAULT_STRING_VAL : fromRegion);
        props.setProperty("dryRun", Boolean.toString(dryRun));

        // Save to file.
        try (OutputStream output = new FileOutputStream(FILENAME)) {
            props.store(output, null);
        } catch (IOException io) {
            return false;
        }
        return true;
    }

    @Override
    public boolean loadProperties() {
        final Properties props = new Properties();

        // Load from file.
        try (InputStream input = new FileInputStream(FILENAME);) {
            props.load(input);
        } catch (IOException ex) {
            clientKey = DEFAULT_STRING_VAL;
            telegramId = DEFAULT_STRING_VAL;
            secretKey = DEFAULT_STRING_VAL;
            lastTelegramType = TelegramType.NORMAL;
            fromRegion = DEFAULT_STRING_VAL;
            dryRun = Boolean.valueOf(DEFAULT_BOOL_VAL);
            return false;
        }

        // Set variables.
        clientKey = props.getProperty("clientKey", DEFAULT_STRING_VAL);
        telegramId = props.getProperty("telegramId", DEFAULT_STRING_VAL);
        secretKey = props.getProperty("secretKey", DEFAULT_STRING_VAL);
        lastTelegramType = valueOf(TelegramType.class, props.getProperty("telegramType"), TelegramType.NORMAL);
        fromRegion = props.getProperty("fromRegion", DEFAULT_STRING_VAL);
        dryRun = Boolean.valueOf(props.getProperty("dryRun", DEFAULT_BOOL_VAL));
        return true;
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

    @Override
    public String getClientKey() {
        return clientKey;
    }

    @Override
    public String getTelegramId() {
        return telegramId;
    }

    @Override
    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public TelegramType getLastTelegramType() {
        return lastTelegramType;
    }

    @Override
    public String getFromRegion() {
        return fromRegion;
    }

    @Override
    public boolean getDoDryRun() {
        return dryRun;
    }

    @Override
    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    @Override
    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    @Override
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void setLastTelegramType(TelegramType lastTelegramType) {
        this.lastTelegramType = lastTelegramType;
    }

    @Override
    public void setFromRegion(String fromRegion) {
        this.fromRegion = fromRegion;
    }

    @Override
    public void setDoDryRun(boolean doDryRun) {
        this.dryRun = doDryRun;
    }
}
