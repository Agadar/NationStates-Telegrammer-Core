package com.github.agadar.telegrammer.core.recipientsprovider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.shard.WorldShard;

import java.util.HashSet;

/**
 * Fetches recently new nations from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NewNationsProvider extends RecipientsProvider {

    public NewNationsProvider(INationStates nationStates) {
        super(nationStates);
    }

    @Override
    public HashSet<String> getRecipients() {
        final World world = nationStates.getWorld(WorldShard.NEWEST_NATIONS).execute();
        return new HashSet<>(world.newestNations);
    }

}
