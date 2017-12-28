package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.worldassembly.WorldAssembly;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

import com.github.agadar.telegrammer.core.util.StringFunctions;

import java.util.HashSet;

/**
 * Fetches recently new World Assembly member nations from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NewWorldAssemblyMembersProvider extends RecipientsProvider {

    public NewWorldAssemblyMembersProvider(INationStates nationStates) {
        super(nationStates);
    }

    @Override
    public HashSet<String> getRecipients() {
        final WorldAssembly worldAssembly = nationStates.getWorldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.RECENT_MEMBER_LOG).execute();
        if (worldAssembly == null || worldAssembly.recentMemberLog == null) {
            return new HashSet<>();
        }
        return StringFunctions.extractNationsFromHappenings(
                worldAssembly.recentMemberLog, StringFunctions.KeyWord.admitted);
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NEW_WORLD_ASSEMBLY_MEMBERS.toString();
    }

}
