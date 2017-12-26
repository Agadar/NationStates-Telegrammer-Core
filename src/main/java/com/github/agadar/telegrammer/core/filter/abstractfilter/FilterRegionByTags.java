package com.github.agadar.telegrammer.core.filter.abstractfilter;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.enumerator.RegionTag;
import com.github.agadar.telegrammer.core.manager.IHistoryManager;
import com.github.agadar.telegrammer.core.util.IFilterCache;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract parent for filter for adding/removing nations that are in/not in
 * regions with/without the supplied tags.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class FilterRegionByTags extends FilterAddOrRemove {

    /**
     * This instance's tags.
     */
    protected final Set<RegionTag> tags;

    public FilterRegionByTags(INationStates nationStates, IHistoryManager historyManager, IFilterCache filterCache, Set<RegionTag> tags, boolean add) {
        super(nationStates, historyManager, filterCache, add);
        this.tags = tags;
    }

    @Override
    public void refresh() {
        // If we already retrieved data before, just return.
        if (nations != null) {
            return;
        }

        // Query global cache for regions mapped to tags.
        Set<String> regions = filterCache.getRegionsToTagsWith(tags);

        // If they aren't in the global cache, retrieve from server and cache them.
        if (regions == null) {
            final World w = getWorld();

            // This check need be done because when no regions are found, the API
            // nevertheless sends back one empty string.
            if (w != null && w.regionsByTag() != null && (w.regionsByTag().size() != 1
                    || !w.regionsByTag().get(0).isEmpty())) {
                regions = new HashSet<>(w.regionsByTag());
            } else {
                regions = new HashSet<>();
            }

            filterCache.mapRegionsToTagsWith(tags, regions);
        }

        // Query global cache. If a region is not found in the global cache,
        // then (download and) read the daily data dump and query the global cache again.
        nations = new HashSet<>();

        for (String region : regions) {
            Set<String> nationsInRegion = filterCache.getNationsInRegion(region);   // Check if global cache contains the values.

            if (nationsInRegion == null) {
                filterCache.importDumpFile();                               // If not, then import dump file.
                nationsInRegion = filterCache.getNationsInRegion(region);   // Check if it contains it now.

                if (nationsInRegion != null) {  // If it does, then add the nations to local cache.
                    nations.addAll(nationsInRegion);
                }
            } else {
                nations.addAll(nationsInRegion);
            }
        }

        cantRetrieveMoreNations = true;
    }

    /**
     * Child-specific implementation of the World data to retrieve.
     *
     * @return
     */
    protected abstract World getWorld();
}
