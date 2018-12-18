package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

import lombok.NonNull;

/**
 * Fetches recently new nations from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NewNationsProvider extends NationStatesRecipientsProvider {

    public NewNationsProvider(@NonNull NationStates nationStates) {
        super(nationStates);
    }

    @Override
    public Collection<String> getRecipients() {
        return nationStates.getWorld(WorldShard.NEWEST_NATIONS).execute().getNewestNations();
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NEW_NATIONS.toString();
    }

}
