package com.github.agadar.telegrammer.core.event;

import java.util.EventObject;
import java.util.Map;

import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;

import lombok.Getter;

/**
 * Published by TelegramManager when the recipients set is refreshed for the
 * next loop.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Getter
public class RecipientsRefreshedEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    /**
     * The filters that failed to be refreshed due to errors while communicating to
     * the API.
     */
    private final Map<RecipientsFilter, NationStatesAPIException> failedFilters;

    public RecipientsRefreshedEvent(Object source, Map<RecipientsFilter, NationStatesAPIException> failedFilters) {
        super(source);
        this.failedFilters = failedFilters;
    }
}
