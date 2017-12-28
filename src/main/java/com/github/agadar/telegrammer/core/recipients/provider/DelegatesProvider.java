package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.worldassembly.WorldAssembly;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

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
        if (worldAssembly == null || worldAssembly.delegates == null) {
            return new HashSet<>();
        }
        return new HashSet<>(worldAssembly.delegates);
    }

    @Override
    public String toString() {
        return RecipientsProviderType.DELEGATES.toString();
    }
}
