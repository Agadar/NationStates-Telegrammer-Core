package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

import lombok.NonNull;

/**
 * Defines usage of NationStates for all recipients providers.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class NationStatesRecipientsProvider extends RecipientsProvider {

    protected final NationStates nationStates;

    public NationStatesRecipientsProvider(
            @NonNull NationStates nationStates,
            @NonNull RecipientsFilterType filterType) {

        super(filterType);
        this.nationStates = nationStates;
    }
}
