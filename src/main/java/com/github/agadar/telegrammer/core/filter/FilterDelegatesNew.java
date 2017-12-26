package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.worldassembly.WorldAssembly;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterHappenings;
import com.github.agadar.telegrammer.core.manager.IHistoryManager;
import com.github.agadar.telegrammer.core.util.IFilterCache;

import java.util.HashSet;

/**
 * Filter for retrieving new delegates. Is never exhausted.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterDelegatesNew extends FilterHappenings {

    public FilterDelegatesNew(INationStates nationStates, IHistoryManager historyManager, IFilterCache filterCache) {
        super(nationStates, historyManager, filterCache, KeyWord.became);
    }

    @Override
    public void refresh() {
        // Get fresh new list from server.
        final WorldAssembly w = nationStates.getWorldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.RECENT_HAPPENINGS).execute();

        // Derive new delegates from happenings, and properly set the local cache.
        nations = this.filterHappenings(new HashSet<>(w.recentHappenings));
    }

    @Override
    public boolean potentiallyInfinite() {
        return true;
    }
}
