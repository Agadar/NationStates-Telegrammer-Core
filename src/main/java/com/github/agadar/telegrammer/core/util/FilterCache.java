package com.github.agadar.telegrammer.core.util;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.DailyDumpNations;
import com.github.agadar.nationstates.domain.nation.Nation;
import com.github.agadar.nationstates.enumerator.DailyDumpMode;
import com.github.agadar.nationstates.enumerator.RegionTag;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Cache used for reducing the number of calls made to the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterCache implements IFilterCache {

    private final Map<String, Set<String>> nationsToRegions;            // nations mapped to regions
    private final Map<Set<RegionTag>, Set<String>> regionsToTagsWith;      // regions mapped to tags they have
    private final Map<Set<RegionTag>, Set<String>> regionsToTagsWithout;   // regions mapped to tags they don't have
    private final Map<String, Set<String>> embassiesToRegions;          // embassy regions mapped to regions
    private boolean hasImportedDumpFile = false;                        // indicates whether or not the dump file has been imported yet

    private final INationStates nationStates;

    private HashSet<String> delegates;   // world assembly delegates
    private HashSet<String> waMembers;   // world assembly members
    private HashSet<String> allNations;         // all nations

    public FilterCache(INationStates nationStates) {
        nationsToRegions = new HashMap<>();
        regionsToTagsWith = new HashMap<>();
        regionsToTagsWithout = new HashMap<>();
        embassiesToRegions = new HashMap<>();
        this.nationStates = nationStates;
    }

    @Override
    public void importDumpFile() {
        // Only if the dump file hasn't already been imported, import the dump file.
        if (hasImportedDumpFile) {
            return;
        }

        DailyDumpNations ddn;

        try {
            ddn = nationStates.getNationDump(DailyDumpMode.READ_LOCAL).execute();
        } catch (Exception ex) {
            // If the exception isn't just a FileNotFoundException, throw this.
            if (ex.getCause().getClass() != FileNotFoundException.class) {
                throw ex;
            }

            // Else, try download the dump file from the server.
            ddn = nationStates.getNationDump(DailyDumpMode.DOWNLOAD_THEN_READ_LOCAL).execute();
        }

        // ddn should now be filled. Use it to fill the caches.
        for (Nation n : ddn.nations) {
            mapNationToRegion(n.regionName, n.name);

            switch (n.worldAssemblyStatus) // Using hard-coded strings for now. Replace with enum once implemented in wrapper.
            {
                case MEMBER:
                    if (waMembers == null) // Instantiate first if set is null.
                    {
                        waMembers = new HashSet<>();
                    }
                    waMembers.add(n.name);              // Now add to WA members.
                    break;
                case DELEGATE:
                    if (delegates == null) // Instantiate first if set is null.
                    {
                        delegates = new HashSet<>();
                    }
                    delegates.add(n.name);              // Now add to WA delegates.
                    break;
            }
        }

        hasImportedDumpFile = true;
    }

    @Override
    public void mapEmbassyToRegion(String region, String embassy) {
        Set<String> embassies = embassiesToRegions.get(region);

        if (embassies == null) {
            embassies = new HashSet<>();
            embassiesToRegions.put(region, embassies);
        }

        embassies.add(embassy);
    }

    @Override
    public void mapEmbassiesToRegion(String region, Set<String> embassies) {
        embassiesToRegions.put(region, embassies);
    }

    @Override
    public Set<String> getEmbassies(String region) {
        return embassiesToRegions.get(region);
    }

    @Override
    public void mapNationToRegion(String region, String nation) {
        Set<String> nations = nationsToRegions.get(region);

        if (nations == null) {
            nations = new HashSet<>();
            nationsToRegions.put(region, nations);
        }

        nations.add(nation);
    }

    @Override
    public void mapNationsToRegion(String region, Set<String> nations) {
        nationsToRegions.put(region, nations);
    }

    @Override
    public Set<String> getNationsInRegion(String region) {
        return nationsToRegions.get(region);
    }

    @Override
    public void mapRegionsToTagsWith(Set<RegionTag> tags, Set<String> regions) {
        regionsToTagsWith.put(tags, regions);
    }

    @Override
    public Set<String> getRegionsToTagsWith(Set<RegionTag> tags) {
        return regionsToTagsWith.get(tags);
    }

    @Override
    public void mapRegionsToTagsWithout(Set<RegionTag> tags, Set<String> regions) {
        regionsToTagsWithout.put(tags, regions);
    }

    @Override
    public Set<String> getRegionsToTagsWithout(Set<RegionTag> tags) {
        return regionsToTagsWithout.get(tags);
    }

    @Override
    public HashSet<String> getAllNations() {
        return this.allNations;
    }

    @Override
    public HashSet<String> getDelegates() {
        return this.delegates;
    }

    @Override
    public HashSet<String> getWaMembers() {
        return this.waMembers;
    }

    @Override
    public void setAllNations(HashSet<String> allNations) {
        this.allNations = allNations;
    }

    @Override
    public void setDelegates(HashSet<String> delegates) {
        this.delegates = delegates;
    }

    @Override
    public void setWaMembers(HashSet<String> waMembers) {
        this.waMembers = waMembers;
    }
}
