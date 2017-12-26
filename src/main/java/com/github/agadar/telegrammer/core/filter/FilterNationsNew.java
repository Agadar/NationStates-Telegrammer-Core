package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.Filter;
import com.github.agadar.telegrammer.core.manager.IHistoryManager;
import com.github.agadar.telegrammer.core.util.IFilterCache;

import java.util.HashSet;

/**
 * Filter for adding new nations to the address set. Is never exhausted.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterNationsNew extends Filter {

    public FilterNationsNew(INationStates nationStates, IHistoryManager historyManager, IFilterCache filterCache) {
        super(nationStates, historyManager, filterCache);
    }

    @Override
    public void refresh() {
        // Get fresh new list from server.
        final World w = nationStates.getWorld(WorldShard.NEWEST_NATIONS).execute();

        // Properly set the local and global caches.
        nations = new HashSet<>(w.newestNations);
    }

    @Override
    public boolean potentiallyInfinite() {
        return true;
    }
}
