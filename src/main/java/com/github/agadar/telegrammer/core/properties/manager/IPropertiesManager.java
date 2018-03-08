package com.github.agadar.telegrammer.core.properties.manager;

import com.github.agadar.telegrammer.core.properties.ApplicationProperties;

/**
 * Assists in saving and loading the property file for this application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface IPropertiesManager<T extends ApplicationProperties> {

    /**
     * Saves the application's properties to the file.
     *
     * @param properties
     * @return True if saving succeeded, false otherwise.
     */
    public boolean saveProperties(T properties);

    /**
     * Loads the application's properties from the file.
     *
     * @param properties
     * @return
     */
    public T loadProperties(T properties);
}
