package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.worldassembly.WorldAssembly;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

import java.util.HashSet;

/**
 * Fetches World Assembly member nations from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class WorldAssemblyMembersProvider extends RecipientsProvider {

    public WorldAssemblyMembersProvider(INationStates nationStates) {
        super(nationStates);
    }

    @Override
    public HashSet<String> getRecipients() {
        final WorldAssembly worldAssembly = nationStates.getWorldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.MEMBERS).execute();
        if (worldAssembly == null || worldAssembly.members == null) {
            return new HashSet<>();
        }
        return new HashSet<>(worldAssembly.members);
    }

    @Override
    public String toString() {
        return RecipientsProviderType.WORLD_ASSEMBLY_MEMBERS.toString();
    }
}
