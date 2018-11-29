package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;

import lombok.NonNull;

/**
 * Defines usage of INationStates for all recipients providers.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class RecipientsProvider implements IRecipientsProvider {

    final protected INationStates nationStates;

    public RecipientsProvider(@NonNull INationStates nationStates) {
        this.nationStates = nationStates;
    }
}
