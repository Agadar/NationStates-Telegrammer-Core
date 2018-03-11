package com.github.agadar.telegrammer.core.regiondumpaccess;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.region.Region;
import com.github.agadar.nationstates.enumerator.DailyDumpMode;
import com.github.agadar.telegrammer.core.util.StringFunctions;

public class RegionDumpAccess implements IRegionDumpAccess {

    /**
     * Nations mapped to regions.
     */
    private Map<String, Set<String>> regionsWithNations;

    /**
     * Keeps track of the last time the dump file was imported.
     */
    private long hasImportedDumpFile = 0;

    private final INationStates nationStates;

    public RegionDumpAccess(INationStates nationStates) {
	this.nationStates = nationStates;
    }

    @Override
    public Set<String> getNationsInRegions(Collection<String> regionNames) {
	this.importDumpFile();
	return regionNames.stream().map((regionName) -> StringFunctions.normalizeName(regionName))
	        .map((regionName) -> regionsWithNations.get(regionName))
	        .filter((nationsInRegion) -> (nationsInRegion != null)).flatMap(nations -> nations.stream())
	        .collect(Collectors.toSet());
    }

    /**
     * Imports the dump file if it hadn't been done already within the last 24
     * hours.
     */
    private void importDumpFile() {
	if (regionsWithNations != null && System.currentTimeMillis() - hasImportedDumpFile < 24 * 60 * 60 * 1000) {
	    return;
	}
	Set<Region> dump;

	try {
	    dump = nationStates.getRegionDump(DailyDumpMode.READ_LOCAL, region -> true).execute();
	} catch (Exception ex) {

	    // If the exception isn't just a FileNotFoundException, throw this.
	    if (ex.getCause().getClass() != FileNotFoundException.class) {
		throw ex;
	    }

	    // If dump file not found, try download it from the server.
	    dump = nationStates.getRegionDump(DailyDumpMode.DOWNLOAD_THEN_READ_LOCAL, region -> true).execute();
	}
	regionsWithNations = dump.stream()
	        .collect(Collectors.toMap(region -> StringFunctions.normalizeName(region.name),
	                region -> region.nationNames != null
	                        ? region.nationNames.stream().map(nation -> StringFunctions.normalizeName(nation))
	                                .collect(Collectors.toSet())
	                        : new HashSet<>()));
	hasImportedDumpFile = System.currentTimeMillis();
    }
}
