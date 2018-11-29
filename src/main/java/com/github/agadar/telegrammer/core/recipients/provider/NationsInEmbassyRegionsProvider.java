package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.enumerator.EmbassyStatus;
import com.github.agadar.nationstates.shard.RegionShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.regiondumpaccess.IRegionDumpAccess;

import lombok.NonNull;

/**
 * Fetches all nations in embassy regions of supplied regions from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInEmbassyRegionsProvider extends RecipientsProviderUsingDump {

    public final Set<String> regionNames;

    public NationsInEmbassyRegionsProvider(@NonNull INationStates nationStates, @NonNull IRegionDumpAccess regionDumpAccess,
            @NonNull Set<String> regionNames) {
        super(nationStates, regionDumpAccess);
        this.regionNames = regionNames;
    }

    @Override
    public Set<String> getRecipients() {
        var embassyRegionsOfRegions = getEmbassyRegionsOfRegions(regionNames);
        return regionDumpAccess.getNationsInRegions(embassyRegionsOfRegions);
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NATIONS_IN_EMBASSY_REGIONS.toString() + " " + regionNames.toString();
    }

    private Set<String> getEmbassyRegionsOfRegions(Collection<String> regionNames) {
        return regionNames.stream()
                .map((regionName) -> getEmbassyRegionsOfRegion(regionName))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private Set<String> getEmbassyRegionsOfRegion(String regionName) {
        return nationStates.getRegion(regionName).shards(RegionShard.EMBASSIES).execute()
                .getEmbassies().stream()
                .filter(embassy -> embassy.getStatus() == EmbassyStatus.ESTABLISHED)
                .map(embassy -> embassy.getRegionName())
                .collect(Collectors.toSet());
    }
}
