package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Set;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.shard.RegionShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

import lombok.NonNull;

/**
 * Fetches all nations that reside in the specified regions from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInRegionsProvider extends RecipientsProvider {

    public final Set<String> regionNames;

    public NationsInRegionsProvider(@NonNull INationStates nationStates, @NonNull Set<String> regionNames) {
        super(nationStates);
        this.regionNames = regionNames;
    }

    @Override
    public Set<String> getRecipients() {
        return regionNames.stream()
                .map(regionName -> getNationsInRegion(regionName))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NATIONS_IN_REGIONS.toString() + " " + regionNames.toString();
    }

    private Set<String> getNationsInRegion(String regionName) {
        return nationStates.getRegion(regionName).shards(RegionShard.NATION_NAMES).execute().getNationNames();
    }

}
