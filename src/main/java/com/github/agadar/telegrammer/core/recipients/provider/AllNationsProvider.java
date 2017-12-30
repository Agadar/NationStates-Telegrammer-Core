package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

import java.util.HashSet;
import java.util.Set;

/**
 * Fetches ALL nation names from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class AllNationsProvider extends RecipientsProvider {

    public AllNationsProvider(INationStates nationStates) {
        super(nationStates);
    }

    @Override
    public Set<String> getRecipients() {
        final World world = nationStates.getWorld(WorldShard.NATIONS).execute();
        if (world == null || world.nations == null) {
            return new HashSet<>();
        }
        return new HashSet<>(world.nations);
    }

    @Override
    public String toString() {
        return RecipientsProviderType.ALL_NATIONS.toString();
    }
}
