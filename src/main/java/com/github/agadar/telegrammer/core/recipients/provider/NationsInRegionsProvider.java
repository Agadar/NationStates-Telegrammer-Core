package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.region.Region;
import com.github.agadar.nationstates.shard.RegionShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

import java.util.HashSet;
import java.util.Set;

/**
 * Fetches all nations that reside in the specified regions from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInRegionsProvider extends RecipientsProvider {

    public final Set<String> regionNames;

    public NationsInRegionsProvider(INationStates nationStates, Set<String> regionNames) {
        super(nationStates);
        this.regionNames = regionNames;
    }

    @Override
    public Set<String> getRecipients() {
        final HashSet<String> nationsInRegions = new HashSet<>();

        regionNames.forEach((regionName) -> {
            nationsInRegions.addAll(getNationsInRegion(regionName));
        });
        return nationsInRegions;
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NATIONS_IN_REGIONS.toString() + " " + regionNames.toString();
    }

    private Set<String> getNationsInRegion(String regionName) {
        final Region region = nationStates.getRegion(regionName).shards(RegionShard.NATION_NAMES).execute();
        if (region == null || region.nationNames == null) {
            return new HashSet<>();
        }
        return new HashSet<>(region.nationNames);
    }

}
