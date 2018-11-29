package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Set;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.enumerator.RegionTag;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.regiondumpaccess.IRegionDumpAccess;

import lombok.NonNull;

/**
 * Fetches nations from regions with specified tags from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInRegionsWithTagsProvider extends RecipientsProviderUsingDump {

    public final Set<RegionTag> regionTags;

    public NationsInRegionsWithTagsProvider(@NonNull INationStates nationStates, @NonNull IRegionDumpAccess regionDumpAccess,
            @NonNull Set<RegionTag> regionTags) {
        super(nationStates, regionDumpAccess);
        this.regionTags = regionTags;
    }

    @Override
    public Set<String> getRecipients() {
        var regionTagsArray = regionTags.toArray(new RegionTag[regionTags.size()]);
        var regionsByTag = nationStates.getWorld(WorldShard.REGIONS_BY_TAG).regionsWithTags(regionTagsArray).execute()
                .getRegionsByTag();
        return regionDumpAccess.getNationsInRegions(regionsByTag);
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NATIONS_IN_REGIONS_WITH_TAGS.toString() + " " + regionTags.toString();
    }
}
