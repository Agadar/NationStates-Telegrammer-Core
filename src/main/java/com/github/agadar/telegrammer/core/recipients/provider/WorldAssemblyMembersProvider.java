package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

import lombok.NonNull;

/**
 * Fetches World Assembly member nations from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class WorldAssemblyMembersProvider extends NationStatesRecipientsProvider {

    public WorldAssemblyMembersProvider(@NonNull NationStates nationStates) {
        super(nationStates, RecipientsFilterType.WORLD_ASSEMBLY_MEMBERS);
    }

    @Override
    public Collection<String> getRecipients() throws NationStatesAPIException {
        return nationStates.getWorldAssembly(Council.SECURITY_COUNCIL).shards(WorldAssemblyShard.MEMBERS).execute()
                .getMembers();
    }
}
