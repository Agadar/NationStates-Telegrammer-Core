package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.enumerator.RegionTag;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.nationdumpaccess.INationDumpAccess;

import java.util.HashSet;

/**
 * Fetches nations from regions with specified tags from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInRegionsWithTagsProvider extends RecipientsProviderUsingDump {

    private final RegionTag[] regionTags;

    public NationsInRegionsWithTagsProvider(INationStates nationStates, INationDumpAccess nationDumpAccess, RegionTag... regionTags) {
        super(nationStates, nationDumpAccess);
        this.regionTags = regionTags;
    }

    @Override
    public HashSet<String> getRecipients() {
        final World world = nationStates.getWorld(WorldShard.REGIONS_BY_TAG).regionsWithTags(regionTags).execute();
        return nationDumpAccess.getNationsInRegions(world.regionsByTag());
    }
}
