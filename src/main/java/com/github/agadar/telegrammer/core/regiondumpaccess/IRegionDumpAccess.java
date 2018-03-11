package com.github.agadar.telegrammer.core.regiondumpaccess;

import java.util.Collection;
import java.util.Set;

/**
 * Cache used for reducing the number of calls made to the API when finding
 * nations within embassy regions of specified regions, or nations within regions
 * that have or do not have specified tags, as it would otherwise take too long
 * to retrieve these.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface IRegionDumpAccess {

    /**
     * Gets all nations in the specified regions.
     *
     * @param regionNames
     * @return
     */
    public Set<String> getNationsInRegions(Collection<String> regionNames);

}