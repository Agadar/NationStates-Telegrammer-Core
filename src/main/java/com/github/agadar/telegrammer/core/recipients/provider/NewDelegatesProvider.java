package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.worldassembly.WorldAssembly;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

import com.github.agadar.telegrammer.core.util.StringFunctions;

import java.util.HashSet;
import java.util.Set;

/**
 * Fetches all recently NEW World Assembly delegates from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NewDelegatesProvider extends RecipientsProvider {

    public NewDelegatesProvider(INationStates nationStates) {
        super(nationStates);
    }

    @Override
    public Set<String> getRecipients() {
        final WorldAssembly worldAssembly = nationStates
                .getWorldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.RECENT_HAPPENINGS)
                .execute();
        if (worldAssembly == null || worldAssembly.recentHappenings == null) {
            return new HashSet<>();
        }
        return StringFunctions.extractNationsFromHappenings(
                worldAssembly.recentHappenings, StringFunctions.KeyWord.became);
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NEW_DELEGATES.toString();
    }

}
