package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.util.StringFunctions;

import lombok.NonNull;

/**
 * Fetches all recently NEW World Assembly delegates from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NewDelegatesProvider extends NationStatesRecipientsProvider {

    public NewDelegatesProvider(@NonNull NationStates nationStates) {
        super(nationStates, RecipientsFilterType.NEW_DELEGATES);
    }

    @Override
    public Collection<String> getRecipients() throws NationStatesAPIException {
        var recentHappenings = nationStates.getWorldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.RECENT_HAPPENINGS).execute()
                .getRecentHappenings();
        return StringFunctions.extractNationsFromHappenings(recentHappenings, StringFunctions.KeyWord.became);
    }
}
