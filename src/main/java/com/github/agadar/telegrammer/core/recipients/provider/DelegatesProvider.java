package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

import lombok.NonNull;

/**
 * Fetches names of all World Assembly delegates from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class DelegatesProvider extends NationStatesRecipientsProvider {

    public DelegatesProvider(@NonNull NationStates nationStates) {
        super(nationStates);
    }

    @Override
    public Collection<String> getRecipients() {
        return nationStates.getWorldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.DELEGATES)
                .execute()
                .getDelegates();
    }

    @Override
    public String toString() {
        return RecipientsProviderType.DELEGATES.toString();
    }
}
