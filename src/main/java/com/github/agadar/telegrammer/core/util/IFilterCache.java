package com.github.agadar.telegrammer.core.util;

import com.github.agadar.nationstates.enumerator.RegionTag;

import java.util.HashSet;
import java.util.Set;

/**
 * Cache used for reducing the number of calls made to the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface IFilterCache {

    public HashSet<String> getAllNations();

    public HashSet<String> getDelegates();

    public HashSet<String> getWaMembers();

    public void setAllNations(HashSet<String> allNations);

    public void setDelegates(HashSet<String> delegates);

    public void setWaMembers(HashSet<String> waMembers);

    public void importDumpFile();

    /**
     * Maps a single embassy region to a region.
     *
     * @param region
     * @param embassy
     */
    public void mapEmbassyToRegion(String region, String embassy);

    /**
     * Maps embassy regions to a region.
     *
     * @param region
     * @param embassies
     */
    public void mapEmbassiesToRegion(String region, Set<String> embassies);

    /**
     * Gets a region's embassy regions.
     *
     * @param region
     * @return
     */
    public Set<String> getEmbassies(String region);

    /**
     * Maps a single nation to a region.
     *
     * @param region
     * @param nation
     */
    public void mapNationToRegion(String region, String nation);

    /**
     * Maps nations to a region.
     *
     * @param region
     * @param nations
     */
    public void mapNationsToRegion(String region, Set<String> nations);

    /**
     * Gets nations in a region.
     *
     * @param region
     * @return
     */
    public Set<String> getNationsInRegion(String region);

    /**
     * Maps regions to tags those regions have.
     *
     * @param tags
     * @param regions
     */
    public void mapRegionsToTagsWith(Set<RegionTag> tags, Set<String> regions);

    /**
     * Gets regions that have the supplied tags.
     *
     * @param tags
     * @return
     */
    public Set<String> getRegionsToTagsWith(Set<RegionTag> tags);

    /**
     * Maps regions to tags those regions DO NOT have.
     *
     * @param tags
     * @param regions
     */
    public void mapRegionsToTagsWithout(Set<RegionTag> tags, Set<String> regions);

    /**
     * Gets regions that DO NOT have the supplied tags.
     *
     * @param tags
     * @return
     */
    public Set<String> getRegionsToTagsWithout(Set<RegionTag> tags);
}
