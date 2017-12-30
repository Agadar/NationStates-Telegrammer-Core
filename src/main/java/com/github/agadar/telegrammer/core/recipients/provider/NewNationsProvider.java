package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

import java.util.HashSet;
import java.util.Set;

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
    public Set<String> getRecipients() {
        final World world = nationStates.getWorld(WorldShard.NEWEST_NATIONS).execute();
        if (world == null || world.newestNations == null) {
            return new HashSet<>();
        }
        return new HashSet<>(world.newestNations);
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NEW_NATIONS.toString();
    }

}
