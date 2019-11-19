package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

import lombok.NonNull;

/**
 * Fetches ALL nation names from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class AllNationsProvider extends NationStatesRecipientsProvider {

    public AllNationsProvider(@NonNull NationStates nationStates) {
        super(nationStates, RecipientsFilterType.ALL_NATIONS);
    }

    @Override
    public Collection<String> getRecipients() throws NationStatesAPIException {
        return nationStates.getWorld(WorldShard.NATIONS).execute().getNations();
    }
}
