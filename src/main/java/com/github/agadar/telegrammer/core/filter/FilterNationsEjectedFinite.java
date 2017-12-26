package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.enumerator.HappeningsFilter;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterHappeningsFinite;
import com.github.agadar.telegrammer.core.manager.IHistoryManager;
import com.github.agadar.telegrammer.core.util.IFilterCache;

import java.util.HashSet;

/**
 * Filter for retrieving X ejected nations, where X >= 0.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterNationsEjectedFinite extends FilterHappeningsFinite {

    public FilterNationsEjectedFinite(INationStates nationStates, IHistoryManager historyManager, IFilterCache filterCache, int amountToRetrieve) {
        super(nationStates, historyManager, filterCache, KeyWord.ejected, amountToRetrieve);
    }

    @Override
    public void refresh() {
        // Get fresh new list from server.
        final World w = nationStates.getWorld(WorldShard.HAPPENINGS)
                .happeningsFilter(HappeningsFilter.EJECT).execute();

        // Derive ejected nations from happenings, and properly set the cache.
        nations = this.filterHappenings(new HashSet<>(w.happenings));
    }
}
