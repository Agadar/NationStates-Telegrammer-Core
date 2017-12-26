package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.enumerator.HappeningsFilter;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterHappenings;
import com.github.agadar.telegrammer.core.manager.IHistoryManager;
import com.github.agadar.telegrammer.core.util.IFilterCache;

import java.util.HashSet;

/**
 * Filter for retrieving ejected nations.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterNationsEjected extends FilterHappenings {

    public FilterNationsEjected(INationStates nationStates, IHistoryManager historyManager, IFilterCache filterCache) {
        super(nationStates, historyManager, filterCache, KeyWord.ejected);
    }

    @Override
    public void refresh() {
        // Get fresh new list from server.
        final World w = nationStates.getWorld(WorldShard.HAPPENINGS)
                .happeningsFilter(HappeningsFilter.EJECT).execute();

        // Derive ejected nations from happenings, and properly set the cache.
        nations = this.filterHappenings(new HashSet<>(w.happenings));
    }

    @Override
    public boolean potentiallyInfinite() {
        return true;
    }
}
