package com.github.agadar.telegrammer.core.recipientsprovider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.worldassembly.WorldAssembly;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;

import java.util.HashSet;

/**
 * Fetches names of all World Assembly delegates from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class DelegatesProvider extends RecipientsProvider {

    public DelegatesProvider(INationStates nationStates) {
        super(nationStates);
    }

    @Override
    public HashSet<String> getRecipients() {
        final WorldAssembly worldAssembly = nationStates
                .getWorldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.DELEGATES)
                .execute();
        return new HashSet<>(worldAssembly.delegates);
    }

}
