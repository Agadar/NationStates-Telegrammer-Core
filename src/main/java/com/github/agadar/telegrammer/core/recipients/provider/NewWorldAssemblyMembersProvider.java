package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;
import com.github.agadar.telegrammer.core.misc.StringFunctions;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

import lombok.NonNull;

/**
 * Fetches recently new World Assembly member nations from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NewWorldAssemblyMembersProvider extends NationStatesRecipientsProvider {

    public NewWorldAssemblyMembersProvider(@NonNull NationStates nationStates) {
        super(nationStates, RecipientsFilterType.NEW_WORLD_ASSEMBLY_MEMBERS);
    }

    @Override
    public Collection<String> getRecipients() throws NationStatesAPIException {
        var recentMemberLog = nationStates.getWorldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.RECENT_MEMBER_LOG).execute().getRecentMemberLog();
        return StringFunctions.extractNationsFromHappenings(recentMemberLog, StringFunctions.KeyWord.admitted);
    }
}
