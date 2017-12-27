package com.github.agadar.telegrammer.core.recipientsprovider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.worldassembly.WorldAssembly;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;

import com.github.agadar.telegrammer.core.util.StringFunctions;

import java.util.HashSet;

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
    public HashSet<String> getRecipients() {
        final WorldAssembly worldAssembly = nationStates
                .getWorldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.RECENT_HAPPENINGS)
                .execute();
        return StringFunctions.extractNationsFromHappenings(
                worldAssembly.recentHappenings, StringFunctions.KeyWord.became);
    }

}
