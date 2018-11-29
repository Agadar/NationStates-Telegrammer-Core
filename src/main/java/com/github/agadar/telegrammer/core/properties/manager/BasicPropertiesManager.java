package com.github.agadar.telegrammer.core.properties.manager;

import com.github.agadar.telegrammer.core.properties.ApplicationProperties;
import com.github.agadar.telegrammer.core.recipients.translator.IRecipientsListBuilderTranslator;

/**
 * The most basic PropertiesManager, completing AbstractPropertiesManager by
 * implementing createApplicationProperties().
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class BasicPropertiesManager extends AbstractPropertiesManager<ApplicationProperties> {

    public BasicPropertiesManager(IRecipientsListBuilderTranslator builderTranslator, String propertiesFileName) {
        super(builderTranslator, propertiesFileName);
    }

    @Override
    protected ApplicationProperties createApplicationProperties() {
        return new ApplicationProperties();
    }

}
