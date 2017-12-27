package com.github.agadar.telegrammer.core.recipients;

/**
 * Filter types, with corresponding String explanations. Can be used for listing
 * filters graphically or in a file, or for translating to actual
 * IRecipientProvider instances.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public enum RecipientsProviderType {
    ALL_NATIONS("all nations"),
    EJECTED_NATIONS("ejected nations"),
    NATIONS_IN_EMBASSY_REGIONS("nations in embassy regions of specified regions"),
    NATIONS_IN_REGIONS_WITH_TAGS("nations in regions with specified tags"),
    NATIONS_IN_REGIONS_WITHOUT_TAGS("nations in regions without specified tags"),
    NATIONS_IN_REGIONS("nations in specified regions"),
    NEW_NATIONS("new nations"),
    NEW_DELEGATES("new World Assembly delegates"),
    NEW_WORLD_ASSEMBLY_MEMBERS("new World Assembly members"),
    REFOUNDED_NATIONS("refounded nations"),
    NATIONS("specified nations"),
    DELEGATES("World Assembly delegates"),
    WORLD_ASSEMBLY_MEMBERS("World Assembly members");

    // Explanation.
    private final String text;

    /**
     * Constructor.
     *
     * @param text
     */
    private RecipientsProviderType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
