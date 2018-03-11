package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.enumerator.RegionTag;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.regiondumpaccess.IRegionDumpAccess;

import java.util.HashSet;
import java.util.Set;

/**
 * Fetches nations from regions with specified tags from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInRegionsWithTagsProvider extends RecipientsProviderUsingDump {

    public final Set<RegionTag> regionTags;

    public NationsInRegionsWithTagsProvider(INationStates nationStates, IRegionDumpAccess regionDumpAccess, Set<RegionTag> regionTags) {
        super(nationStates, regionDumpAccess);
        this.regionTags = regionTags;
    }

    @Override
    public Set<String> getRecipients() {
        final World world = nationStates
                .getWorld(WorldShard.REGIONS_BY_TAG)
                .regionsWithTags(regionTags.toArray(new RegionTag[regionTags.size()]))
                .execute();
        if (world == null || world.regionsByTag() == null) {
            return new HashSet<>();
        }
        return regionDumpAccess.getNationsInRegions(world.regionsByTag());
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NATIONS_IN_REGIONS_WITH_TAGS.toString() + " " + regionTags.toString();
    }
}
