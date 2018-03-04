package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.region.Region;
import com.github.agadar.nationstates.enumerator.EmbassyStatus;
import com.github.agadar.nationstates.shard.RegionShard;
import com.github.agadar.telegrammer.core.nationdumpaccess.INationDumpAccess;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

/**
 * Fetches all nations in embassy regions of supplied regions from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsInEmbassyRegionsProvider extends RecipientsProviderUsingDump {

    public final Set<String> regionNames;

    public NationsInEmbassyRegionsProvider(INationStates nationStates, INationDumpAccess nationDumpAccess,
	    Set<String> regionNames) {
	super(nationStates, nationDumpAccess);
	this.regionNames = regionNames;
    }

    @Override
    public Set<String> getRecipients() {
	final Set<String> embassyRegionsOfRegions = getEmbassyRegionsOfRegions(regionNames);
	return nationDumpAccess.getNationsInRegions(embassyRegionsOfRegions);
    }

    @Override
    public String toString() {
	return RecipientsProviderType.NATIONS_IN_EMBASSY_REGIONS.toString() + " " + regionNames.toString();
    }

    private Set<String> getEmbassyRegionsOfRegions(Collection<String> regionNames) {
	final HashSet<String> embassyRegionsOfRegions = new HashSet<>();

	regionNames.forEach((regionName) -> {
	    embassyRegionsOfRegions.addAll(getEmbassyRegionsOfRegion(regionName));
	});
	return embassyRegionsOfRegions;
    }

    private Set<String> getEmbassyRegionsOfRegion(String regionName) {
	final Region region = nationStates.getRegion(regionName).shards(RegionShard.EMBASSIES).execute();
	if (region == null || region.embassies == null) {
	    return new HashSet<>();
	}
	return region.embassies.stream().filter(embassy -> embassy.status == EmbassyStatus.ESTABLISHED)
		.map(embassy -> embassy.regionName).collect(Collectors.toSet());
    }

}
