package com.github.agadar.telegrammer.core.recipientsprovider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.shard.WorldShard;

import java.util.HashSet;

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
    public HashSet<String> getRecipients() {
        final World world = nationStates.getWorld(WorldShard.NATIONS).execute();
        return new HashSet<>(world.nations);
    }
}
