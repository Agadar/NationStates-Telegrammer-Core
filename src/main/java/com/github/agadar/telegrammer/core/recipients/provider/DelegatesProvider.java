package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

import lombok.NonNull;

/**
 * Fetches names of all World Assembly delegates from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class DelegatesProvider extends NationStatesRecipientsProvider {

    public DelegatesProvider(@NonNull NationStates nationStates) {
        super(nationStates, RecipientsFilterType.DELEGATES);
    }

    @Override
    public Collection<String> getRecipients() throws NationStatesAPIException {
        return nationStates.getWorldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.DELEGATES)
                .execute()
                .getDelegates();
    }
}
