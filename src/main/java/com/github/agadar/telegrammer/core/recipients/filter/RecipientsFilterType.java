package com.github.agadar.telegrammer.core.recipients.filter;

import lombok.Getter;

/**
 * Filter types, with corresponding textual descriptions and supported actions.
 * Can be used for listing filters graphically or in a file, or for translating
 * to actual {@link RecipientsFilter} instances.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public enum RecipientsFilterType {

    ALL_NATIONS("All nations"),
    EJECTED_NATIONS("Ejected nations"),
    NATIONS_IN_EMBASSY_REGIONS("Nations in embassies of specified regions"),
    NATIONS_IN_REGIONS_WITH_TAGS("Nations in regions with specified tags"),
    NATIONS_IN_REGIONS_WITHOUT_TAGS("Nations in regions without specified tags"),
    NATIONS_IN_REGIONS("Nations in specified regions"),
    NATIONS_WITH_NUMBERS("Nations with numbers in name", RecipientsFilterAction.REMOVE_FROM_RECIPIENTS),
    NEW_NATIONS("New nations"),
    NEW_DELEGATES("New World Assembly delegates"),
    NEW_WORLD_ASSEMBLY_MEMBERS("New World Assembly members"),
    REFOUNDED_NATIONS("Refounded nations"),
    NATIONS("Specified nations"),
    DELEGATES("World Assembly delegates"),
    WORLD_ASSEMBLY_MEMBERS("World Assembly members");

    private final String text;
    
    @Getter
    private final RecipientsFilterAction[] supportedActions;

    private RecipientsFilterType(String text) {
        this(text, new RecipientsFilterAction[] {
                RecipientsFilterAction.ADD_TO_RECIPIENTS,
                RecipientsFilterAction.REMOVE_FROM_RECIPIENTS,
                RecipientsFilterAction.REMOVE_RECIPIENTS_NOT_IN });
    }

    private RecipientsFilterType(String text, RecipientsFilterAction supportedAction) {
        this(text, new RecipientsFilterAction[] { supportedAction });
    }

    private RecipientsFilterType(String text, RecipientsFilterAction[] supportedActions) {
        this.text = text;
        this.supportedActions = supportedActions;
    }

    @Override
    public String toString() {
        return text;
    }
}
