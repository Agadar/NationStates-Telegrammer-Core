package com.github.agadar.telegrammer.core.event;

import java.util.EventObject;
import java.util.Map;

import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.telegrammer.core.Telegrammer;
import com.github.agadar.telegrammer.core.misc.TelegrammerState;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;

import lombok.Getter;

/**
 * Published by {@link Telegrammer} when the recipients set is refreshed for the
 * next loop while queuing telegrams.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Getter
public class FinishedRefreshingRecipientsEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    /**
     * The state the {@link Telegrammer} is in when this event was fired.
     */
    private final TelegrammerState telegrammerState;
    /**
     * The filters that failed to be refreshed due to errors while communicating to
     * the API.
     */
    private final Map<RecipientsFilter, NationStatesAPIException> failedFilters;

    public FinishedRefreshingRecipientsEvent(Object source, TelegrammerState telegrammerState,
            Map<RecipientsFilter, NationStatesAPIException> failedFilters) {
        super(source);
        this.telegrammerState = telegrammerState;
        this.failedFilters = failedFilters;
    }
}
