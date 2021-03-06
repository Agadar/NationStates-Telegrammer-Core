package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

import lombok.NonNull;

/**
 * Fetches recently new nations from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NewNationsProvider extends NationStatesRecipientsProvider {

    public NewNationsProvider(@NonNull NationStates nationStates) {
        super(nationStates, RecipientsFilterType.NEW_NATIONS);
    }

    @Override
    public Collection<String> getRecipients() throws NationStatesAPIException {
        return nationStates.getWorld(WorldShard.NEWEST_NATIONS).execute().getNewestNations();
    }
}
