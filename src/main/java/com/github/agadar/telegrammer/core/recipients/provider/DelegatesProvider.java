package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Set;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

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
    public Set<String> getRecipients() {
        return nationStates.getWorldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.DELEGATES)
                .execute()
                .getDelegates();
    }

    @Override
    public String toString() {
        return RecipientsProviderType.DELEGATES.toString();
    }
}
