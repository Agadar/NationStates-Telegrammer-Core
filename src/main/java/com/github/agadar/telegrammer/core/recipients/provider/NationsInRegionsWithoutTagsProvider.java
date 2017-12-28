package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.enumerator.RegionTag;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.nationdumpaccess.INationDumpAccess;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

import java.util.HashSet;

/**
 * Fetches nations from regions WITHOUT specified tags from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInRegionsWithoutTagsProvider extends RecipientsProviderUsingDump {

    private final HashSet<RegionTag> regionTags;

    public NationsInRegionsWithoutTagsProvider(INationStates nationStates, INationDumpAccess nationDumpAccess, HashSet<RegionTag> regionTags) {
        super(nationStates, nationDumpAccess);
        this.regionTags = regionTags;
    }

    @Override
    public HashSet<String> getRecipients() {
        final World world = nationStates
                .getWorld(WorldShard.REGIONS_BY_TAG)
                .regionsWithoutTags(regionTags.toArray(new RegionTag[regionTags.size()]))
                .execute();
        if (world == null || world.regionsByTag() == null) {
            return new HashSet<>();
        }
        return nationDumpAccess.getNationsInRegions(world.regionsByTag());
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NATIONS_IN_REGIONS_WITHOUT_TAGS.toString() + " " + regionTags.toString();
    }
}
