package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.enumerator.RegionTag;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccess;

import lombok.NonNull;

/**
 * Fetches nations from regions WITHOUT specified tags from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInRegionsWithoutTagsProvider extends RecipientsProviderUsingDump {

    public final Collection<RegionTag> regionTags;

    public NationsInRegionsWithoutTagsProvider(@NonNull NationStates nationStates, @NonNull RegionDumpAccess regionDumpAccess,
            @NonNull Collection<RegionTag> regionTags) {
        super(nationStates, regionDumpAccess);
        this.regionTags = regionTags;
    }

    @Override
    public Collection<String> getRecipients() {
        var regionTagsArray = regionTags.toArray(new RegionTag[regionTags.size()]);
        var regionsByTag = nationStates.getWorld(WorldShard.REGIONS_BY_TAG).regionsWithoutTags(regionTagsArray)
                .execute().getRegionsByTag();
        return regionDumpAccess.getNationsInRegions(regionsByTag);
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NATIONS_IN_REGIONS_WITHOUT_TAGS.toString() + " " + regionTags.toString();
    }
}
