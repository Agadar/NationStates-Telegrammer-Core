package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.enumerator.RegionTag;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccess;

import lombok.NonNull;

/**
 * Fetches nations from regions with specified tags from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInRegionsWithTagsProvider extends RecipientsProviderUsingDump {

    public final Collection<RegionTag> regionTags;

    public NationsInRegionsWithTagsProvider(@NonNull NationStates nationStates, @NonNull RegionDumpAccess regionDumpAccess,
            @NonNull Collection<RegionTag> regionTags) {
        super(nationStates, regionDumpAccess);
        this.regionTags = regionTags;
    }

    @Override
    public Collection<String> getRecipients() {
        var regionTagsArray = regionTags.toArray(new RegionTag[regionTags.size()]);
        var regionsByTag = nationStates.getWorld(WorldShard.REGIONS_BY_TAG).regionsWithTags(regionTagsArray).execute()
                .getRegionsByTag();
        return regionDumpAccess.getNationsInRegions(regionsByTag);
    }

    @Override
    public String toString() {
        return RecipientsFilterType.NATIONS_IN_REGIONS_WITH_TAGS.toString() + " " + regionTags.toString();
    }
}
