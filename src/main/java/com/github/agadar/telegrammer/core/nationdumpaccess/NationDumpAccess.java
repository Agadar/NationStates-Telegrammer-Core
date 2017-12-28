package com.github.agadar.telegrammer.core.nationdumpaccess;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.DailyDumpNations;
import com.github.agadar.nationstates.domain.nation.Nation;
import com.github.agadar.nationstates.enumerator.DailyDumpMode;

import com.github.agadar.telegrammer.core.util.StringFunctions;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

// TODO: keep track of timestamp to refresh every day or so.
public class NationDumpAccess implements INationDumpAccess {

    /**
     * Nations mapped to regions.
     */
    private final Map<String, HashSet<String>> nationsToRegions;

    /**
     * Keeps track of whether or not the dump file has been imported yet
     */
    private boolean hasImportedDumpFile = false;
    
    private final INationStates nationStates;
    
    public NationDumpAccess(INationStates nationStates) {
        nationsToRegions = new HashMap<>();
        this.nationStates = nationStates;
    }
    
    @Override
    public HashSet<String> getNationsInRegions(Collection<String> regionNames) {
        this.importDumpFile();
        final HashSet<String> nationsInRegions = new HashSet<>();
        regionNames.stream()
                .map((regionName) -> StringFunctions.normalizeName(regionName))
                .map((regionName) -> nationsToRegions.get(regionName))
                .filter((nationsInRegion) -> (nationsInRegion != null))
                .forEach((nationsInRegion) -> {
                    nationsInRegions.addAll(nationsInRegion);
                });
        return nationsInRegions;
    }
    
    private void importDumpFile() {
        if (hasImportedDumpFile) {
            return;
        }
        DailyDumpNations dump;
        
        try {
            dump = nationStates.getNationDump(DailyDumpMode.READ_LOCAL).execute();
        } catch (Exception ex) {

            // If the exception isn't just a FileNotFoundException, throw this.
            if (ex.getCause().getClass() != FileNotFoundException.class) {
                throw ex;
            }

            // If dump file not found, try download it from the server.
            dump = nationStates.getNationDump(DailyDumpMode.DOWNLOAD_THEN_READ_LOCAL).execute();
        }
        
        for (Nation nation : dump.nations) {
            mapNationToRegion(nation.regionName, nation.name);
        }
        hasImportedDumpFile = true;
    }
    
    private void mapNationToRegion(String region, String nation) {
        region = StringFunctions.normalizeName(region);
        nation = StringFunctions.normalizeName(nation);
        
        HashSet<String> nations = nationsToRegions.get(region);
        
        if (nations == null) {
            nations = new HashSet<>();
            nationsToRegions.put(region, nations);
        }
        nations.add(nation);
    }
}
