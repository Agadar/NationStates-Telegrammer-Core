package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.NationStates;

import lombok.NonNull;

/**
 * Defines usage of NationStates for all recipients providers.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class NationStatesRecipientsProvider implements RecipientsProvider {

    final protected NationStates nationStates;

    public NationStatesRecipientsProvider(@NonNull NationStates nationStates) {
        this.nationStates = nationStates;
    }
}
