package com.github.agadar.telegrammer.core.recipients;

/**
 * Filter types, with corresponding String explanations. Can be used for listing
 * filters graphically or in a file, or for translating to actual
 * IRecipientProvider instances.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public enum RecipientsProviderType {
    ALL_NATIONS("All nations"),
    EJECTED_NATIONS("Ejected nations"),
    NATIONS_IN_EMBASSY_REGIONS("Nations in embassy regions of specified regions"),
    NATIONS_IN_REGIONS_WITH_TAGS("Nations in regions with specified tags"),
    NATIONS_IN_REGIONS_WITHOUT_TAGS("Nations in regions without specified tags"),
    NATIONS_IN_REGIONS("Nations in specified regions"),
    NEW_NATIONS("New nations"),
    NEW_DELEGATES("New World Assembly delegates"),
    NEW_WORLD_ASSEMBLY_MEMBERS("New World Assembly members"),
    REFOUNDED_NATIONS("Refounded nations"),
    NATIONS("Specified nations"),
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
