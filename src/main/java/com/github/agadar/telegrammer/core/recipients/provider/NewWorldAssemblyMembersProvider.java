package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Set;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.util.StringFunctions;

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
    public Set<String> getRecipients() {
        var recentMemberLog = nationStates.getWorldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.RECENT_MEMBER_LOG).execute().getRecentMemberLog();
        return StringFunctions.extractNationsFromHappenings(recentMemberLog, StringFunctions.KeyWord.admitted);
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NEW_WORLD_ASSEMBLY_MEMBERS.toString();
    }

}
