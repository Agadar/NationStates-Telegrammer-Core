package com.github.agadar.telegrammer.core.regiondumpaccess;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.region.Region;
import com.github.agadar.nationstates.enumerator.DailyDumpMode;
import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.telegrammer.core.util.StringFunctions;

import lombok.NonNull;

public class RegionDumpAccessImpl implements RegionDumpAccess {

    /**
     * Nations mapped to regions.
     */
    private Map<String, Collection<String>> regionsWithNations;

    /**
     * Keeps track of the last time the dump file was imported.
     */
    private long hasImportedDumpFile = 0;

    private final NationStates nationStates;

    public RegionDumpAccessImpl(@NonNull NationStates nationStates) {
        this.nationStates = nationStates;
    }

    @Override
    public Collection<String> getNationsInRegions(@NonNull Collection<String> regionNames)
            throws NationStatesAPIException {
        this.importDumpFile();
        return regionNames.stream()
                .map((regionName) -> StringFunctions.normalizeName(regionName))
                .map((regionName) -> regionsWithNations.get(regionName))
                .filter((nationsInRegion) -> (nationsInRegion != null))
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void importDumpFile() throws NationStatesAPIException {
        if (regionsWithNations != null && System.currentTimeMillis() - hasImportedDumpFile < 24 * 60 * 60 * 1000) {
            return;
        }
        Collection<Region> dump;

        try {
            dump = nationStates.getRegionDump(DailyDumpMode.READ_LOCAL, region -> true).execute();
        } catch (NationStatesAPIException ex) {

            // If the exception isn't just a FileNotFoundException, throw this.
            if (ex.getCause().getClass() != FileNotFoundException.class) {
                throw ex;
            }

            // If dump file not found, try download it from the server.
            dump = nationStates.getRegionDump(DailyDumpMode.DOWNLOAD_THEN_READ_LOCAL, region -> true).execute();
        }
        regionsWithNations = dump.stream()
                .collect(Collectors.toMap(region -> StringFunctions.normalizeName(region.getName()),
                        region -> region.getNationNames().stream().map(nation -> StringFunctions.normalizeName(nation))
                                .collect(Collectors.toCollection(LinkedHashSet::new))));
        hasImportedDumpFile = System.currentTimeMillis();
    }
}
