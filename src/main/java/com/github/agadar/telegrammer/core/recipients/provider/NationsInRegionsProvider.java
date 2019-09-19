package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.shard.RegionShard;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

import lombok.NonNull;

/**
 * Fetches all nations that reside in the specified regions from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInRegionsProvider extends NationStatesRecipientsProvider {

    public final Collection<String> regionNames;

    public NationsInRegionsProvider(@NonNull NationStates nationStates, @NonNull Collection<String> regionNames) {
        super(nationStates);
        this.regionNames = regionNames;
    }

    @Override
    public Collection<String> getRecipients() {
        return regionNames.stream()
                .map(regionName -> getNationsInRegion(regionName))
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public String toString() {
        return RecipientsFilterType.NATIONS_IN_REGIONS.toString() + " " + regionNames.toString();
    }

    private Collection<String> getNationsInRegion(String regionName) {
        return nationStates.getRegion(regionName).shards(RegionShard.NATION_NAMES).execute().getNationNames();
    }

}
