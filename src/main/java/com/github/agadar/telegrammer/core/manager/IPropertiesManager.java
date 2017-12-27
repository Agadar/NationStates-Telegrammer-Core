package com.github.agadar.telegrammer.core.manager;

import com.github.agadar.telegrammer.core.enums.TelegramType;

/**
 * Assists in saving and loading the property file for this application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface IPropertiesManager {

    public String getClientKey();

    public String getTelegramId();

    public String getSecretKey();

    public TelegramType getLastTelegramType();

    public String getFromRegion();

    public boolean getContinueIndefinitely();

    public void setClientKey(String clientKey);

    public void setTelegramId(String telegramId);

    public void setSecretKey(String secretKey);

    public void setLastTelegramType(TelegramType lastTelegramType);

    public void setFromRegion(String fromRegion);

    public void setContinueIndefinitely(boolean continueIndefinitely);

    /**
     * Saves the application's properties to the file.
     *
     * @return True if saving succeeded, false otherwise.
     */
    public boolean saveProperties();

    /**
     * Loads the application's properties from the file.
     *
     * @return True if loading succeeded, false otherwise.
     */
    public boolean loadProperties();
}
