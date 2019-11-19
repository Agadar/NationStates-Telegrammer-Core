package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.nationstates.shard.RegionShard;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * Fetches all nations that reside in the specified regions from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInRegionsProvider extends NationStatesRecipientsProvider {

    private final Collection<String> regionNames;

    public NationsInRegionsProvider(@NonNull NationStates nationStates, @NonNull Collection<String> regionNames) {
        super(nationStates, RecipientsFilterType.NATIONS_IN_REGIONS);
        this.regionNames = regionNames;
    }

    @Override
    public Collection<String> getRecipients() throws NationStatesAPIException {
        return regionNames.stream()
                .map(regionName -> getNationsInRegion(regionName))
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public String toString() {
        return super.toString() + " " + regionNames.toString();
    }

    @Override
    public String toConfigurationString() {
        return super.toConfigurationString() + regionNames.toString();
    }

    @SneakyThrows
    private Collection<String> getNationsInRegion(String regionName) {
        return nationStates.getRegion(regionName).shards(RegionShard.NATION_NAMES).execute().getNationNames();
    }

}
