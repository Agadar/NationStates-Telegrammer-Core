package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.enumerator.HappeningsFilter;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.util.StringFunctions;

import java.util.HashSet;

/**
 * Fetches recently refounded nations from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RefoundedNationsProvider extends RecipientsProvider {

    public RefoundedNationsProvider(INationStates nationStates) {
        super(nationStates);
    }

    @Override
    public HashSet<String> getRecipients() {
        final World world = nationStates.getWorld(WorldShard.HAPPENINGS)
                .happeningsFilter(HappeningsFilter.FOUNDING).execute();
        return StringFunctions.extractNationsFromHappenings(world.happenings, StringFunctions.KeyWord.refounded);
    }

}
