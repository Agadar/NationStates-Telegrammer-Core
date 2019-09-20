package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.enumerator.EmbassyStatus;
import com.github.agadar.nationstates.shard.RegionShard;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccess;

import lombok.NonNull;

/**
 * Fetches all nations in embassy regions of supplied regions from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInEmbassyRegionsProvider extends RecipientsProviderUsingDump {

    private final Collection<String> regionNames;

    public NationsInEmbassyRegionsProvider(
            @NonNull NationStates nationStates,
            @NonNull RegionDumpAccess regionDumpAccess,
            @NonNull Collection<String> regionNames) {

        super(nationStates, regionDumpAccess, RecipientsFilterType.NATIONS_IN_EMBASSY_REGIONS);
        this.regionNames = regionNames;
    }

    @Override
    public Collection<String> getRecipients() {
        var embassyRegionsOfRegions = getEmbassyRegionsOfRegions(regionNames);
        return regionDumpAccess.getNationsInRegions(embassyRegionsOfRegions);
    }

    @Override
    public String toString() {
        return super.toString() + " " + regionNames.toString();
    }

    @Override
    public String toConfigurationString() {
        return super.toConfigurationString() + regionNames.toString();
    }

    private Collection<String> getEmbassyRegionsOfRegions(Collection<String> regionNames) {
        return regionNames.stream()
                .map((regionName) -> getEmbassyRegionsOfRegion(regionName))
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Collection<String> getEmbassyRegionsOfRegion(String regionName) {
        return nationStates.getRegion(regionName).shards(RegionShard.EMBASSIES).execute()
                .getEmbassies().stream()
                .filter(embassy -> embassy.getStatus() == EmbassyStatus.ESTABLISHED)
                .map(embassy -> embassy.getRegionName())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
