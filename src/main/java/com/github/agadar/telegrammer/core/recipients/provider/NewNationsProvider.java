package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.LinkedHashSet;
import java.util.Set;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

import lombok.NonNull;

/**
 * Fetches recently new nations from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NewNationsProvider extends RecipientsProvider {

    public NewNationsProvider(@NonNull INationStates nationStates) {
        super(nationStates);
    }

    @Override
    public Set<String> getRecipients() {
        return new LinkedHashSet<>(nationStates.getWorld(WorldShard.NEWEST_NATIONS).execute().getNewestNations());
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NEW_NATIONS.toString();
    }

}
