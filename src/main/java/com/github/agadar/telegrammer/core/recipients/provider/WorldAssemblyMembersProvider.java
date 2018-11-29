package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Set;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

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
    public Set<String> getRecipients() {
        return nationStates.getWorldAssembly(Council.SECURITY_COUNCIL).shards(WorldAssemblyShard.MEMBERS).execute()
                .getMembers();
    }

    @Override
    public String toString() {
        return RecipientsProviderType.WORLD_ASSEMBLY_MEMBERS.toString();
    }
}
