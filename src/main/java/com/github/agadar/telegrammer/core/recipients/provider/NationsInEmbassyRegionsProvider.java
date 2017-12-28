package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.region.Embassy;
import com.github.agadar.nationstates.domain.region.Region;
import com.github.agadar.nationstates.enumerator.EmbassyStatus;
import com.github.agadar.nationstates.shard.RegionShard;

import com.github.agadar.telegrammer.core.nationdumpaccess.INationDumpAccess;

import java.util.HashSet;
import java.util.Collection;

/**
 * Fetches all nations in embassy regions of supplied regions from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInEmbassyRegionsProvider extends RecipientsProviderUsingDump {

    private final HashSet<String> regionNames;

    public NationsInEmbassyRegionsProvider(INationStates nationStates, INationDumpAccess nationDumpAccess, HashSet<String> regionNames) {
        super(nationStates, nationDumpAccess);
        this.regionNames = regionNames;
    }

    @Override
    public HashSet<String> getRecipients() {
        final HashSet<String> embassyRegionsOfRegions = getEmbassyRegionsOfRegions(regionNames);
        return nationDumpAccess.getNationsInRegions(embassyRegionsOfRegions);
    }

    private HashSet<String> getEmbassyRegionsOfRegions(Collection<String> regionNames) {
        final HashSet<String> embassyRegionsOfRegions = new HashSet<>();

        regionNames.forEach((regionName) -> {
            embassyRegionsOfRegions.addAll(getEmbassyRegionsOfRegion(regionName));
        });
        return embassyRegionsOfRegions;
    }

    private HashSet<String> getEmbassyRegionsOfRegion(String regionName) {
        final Region region = nationStates.getRegion(regionName).shards(RegionShard.EMBASSIES).execute();
        if (region == null || region.embassies == null) {
            return new HashSet<>();
        }
        return extractEstablishedEmbassyRegionNames(region.embassies);
    }

    /**
     * Extracts all ESTABLISHED embassies from the given collection and returns
     * the names of the regions those extracted embassies are in.
     *
     * @param embassies
     * @return
     */
    private HashSet<String> extractEstablishedEmbassyRegionNames(Collection<Embassy> embassies) {
        final HashSet<String> establishedEmbassyRegionNames = new HashSet<>();
        embassies.forEach((embassy) -> {
            if (embassy.status == EmbassyStatus.ESTABLISHED) {
                establishedEmbassyRegionNames.add(embassy.regionName);
            }
        });
        return establishedEmbassyRegionNames;
    }

}
