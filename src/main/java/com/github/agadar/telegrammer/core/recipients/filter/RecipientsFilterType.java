package com.github.agadar.telegrammer.core.recipients.filter;

import java.util.Arrays;
import java.util.Collection;

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
    NATIONS_CONTAINING_KEYWORDS("Nations containing specific keywords", RecipientsFilterAction.REMOVE_FROM_RECIPIENTS,
            "Insert keywords, e.g. 'noob', '420'."),
    NATIONS_IN_EMBASSY_REGIONS("Nations in embassies of specified regions",
            "Insert region names, e.g. 'region1, region2'."),
    NATIONS_IN_REGIONS_WITH_TAGS("Nations in regions with specified tags", "Insert region tags, e.g. 'tag1, tag2'."),
    NATIONS_IN_REGIONS_WITHOUT_TAGS("Nations in regions without specified tags",
            "Insert region tags, e.g. 'tag1, tag2'."),
    NATIONS_IN_REGIONS("Nations in specified regions", "Insert region names, e.g. 'region1, region2'."),
    NATIONS_WITH_NUMBERS("Nations with numbers in name", RecipientsFilterAction.REMOVE_FROM_RECIPIENTS),
    NEW_NATIONS("New nations"),
    NEW_DELEGATES("New World Assembly delegates"),
    NEW_WORLD_ASSEMBLY_MEMBERS("New World Assembly members"),
    REFOUNDED_NATIONS("Refounded nations"),
    NATIONS("Specified nations", "Insert nation names, e.g. 'nation1, nation2'."),
    DELEGATES("World Assembly delegates"),
    WORLD_ASSEMBLY_MEMBERS("World Assembly members");

    private final String text;
    @Getter
    private final Collection<RecipientsFilterAction> supportedActions;
    @Getter
    private final boolean allowsInput;
    @Getter
    private final String inputHint;

    private RecipientsFilterType(String text) {
        this(text, "");
    }

    private RecipientsFilterType(String text, String inputHint) {
        this(text, !inputHint.isEmpty(), Arrays.asList(
                RecipientsFilterAction.ADD_TO_RECIPIENTS,
                RecipientsFilterAction.REMOVE_FROM_RECIPIENTS,
                RecipientsFilterAction.REMOVE_RECIPIENTS_NOT_IN), inputHint);
    }

    private RecipientsFilterType(String text, RecipientsFilterAction supportedAction) {
        this(text, supportedAction, "");
    }

    private RecipientsFilterType(String text, RecipientsFilterAction supportedAction, String inputHint) {
        this(text, !inputHint.isEmpty(), Arrays.asList(supportedAction), inputHint);
    }

    private RecipientsFilterType(String text, boolean allowsInput, Collection<RecipientsFilterAction> supportedActions,
            String inputHint) {
        this.text = text;
        this.allowsInput = allowsInput;
        this.supportedActions = supportedActions;
        this.inputHint = inputHint;
    }

    @Override
    public String toString() {
        return text;
    }
}
