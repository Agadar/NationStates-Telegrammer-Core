package com.github.agadar.telegrammer.core.recipientsprovider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.enumerator.HappeningsFilter;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.util.StringFunctions;

import java.util.HashSet;

/**
 * Fetches recently ejected nations from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class EjectedNationsProvider extends RecipientsProvider {

    public EjectedNationsProvider(INationStates nationStates) {
        super(nationStates);
    }

    @Override
    public HashSet<String> getRecipients() {
        final World world = nationStates.getWorld(WorldShard.HAPPENINGS)
                .happeningsFilter(HappeningsFilter.EJECT).execute();
        return StringFunctions.extractNationsFromHappenings(world.happenings, StringFunctions.KeyWord.ejected);
    }

}
