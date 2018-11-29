package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Set;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

import lombok.NonNull;

/**
 * Fetches ALL nation names from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class AllNationsProvider extends RecipientsProvider {

    public AllNationsProvider(@NonNull INationStates nationStates) {
        super(nationStates);
    }

    @Override
    public Set<String> getRecipients() {
        return nationStates.getWorld(WorldShard.NATIONS).execute().getNations();
    }

    @Override
    public String toString() {
        return RecipientsProviderType.ALL_NATIONS.toString();
    }
}
