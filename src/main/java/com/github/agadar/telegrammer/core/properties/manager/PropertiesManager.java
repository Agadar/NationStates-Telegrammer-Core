package com.github.agadar.telegrammer.core.properties.manager;

import com.github.agadar.telegrammer.core.properties.ApplicationProperties;

/**
 * Assists in saving and loading the property file for this application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface PropertiesManager<T extends ApplicationProperties> {

    /**
     * Persists the application's properties to the file system.
     *
     * @return True if saving succeeded, false otherwise.
     */
    public boolean persistPropertiesToFileSystem();

    /**
     * Loads the application's properties from the file system and caches them.
     *
     * @param properties
     * @return
     */
    public T loadPropertiesFromFileSystem();

    /**
     * Gets the cached application properties.
     * 
     * @return
     */
    public T getProperties();
}
