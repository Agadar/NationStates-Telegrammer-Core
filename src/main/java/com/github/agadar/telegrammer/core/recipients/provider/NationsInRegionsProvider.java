package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.region.Region;
import com.github.agadar.nationstates.shard.RegionShard;

import java.util.HashSet;

/**
 * Fetches all nations that reside in the specified regions from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInRegionsProvider extends RecipientsProvider {

    private final HashSet<String> regionNames;

    public NationsInRegionsProvider(INationStates nationStates, HashSet<String> regionNames) {
        super(nationStates);
        this.regionNames = regionNames;
    }

    @Override
    public HashSet<String> getRecipients() {
        final HashSet<String> nationsInRegions = new HashSet<>();

        regionNames.forEach((regionName) -> {
            nationsInRegions.addAll(getNationsInRegion(regionName));
        });
        return nationsInRegions;
    }

    private HashSet<String> getNationsInRegion(String regionName) {
        final Region region = nationStates.getRegion(regionName).shards(RegionShard.NATION_NAMES).execute();
        return new HashSet<>(region.nationNames);
    }

}
