package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.worldassembly.WorldAssembly;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterAddOrRemove;
import com.github.agadar.telegrammer.core.manager.IHistoryManager;
import com.github.agadar.telegrammer.core.util.IFilterCache;

import java.util.HashSet;

/**
 * Filter for adding/removing World Assembly members from the address set.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterWAMembers extends FilterAddOrRemove {

    public FilterWAMembers(INationStates nationStates, IHistoryManager historyManager, IFilterCache filterCache, boolean add) {
        super(nationStates, historyManager, filterCache, add);
    }

    @Override
    public void refresh() {
        // If we already retrieved data before, do nothing..
        if (nations != null) {
            return;
        }
        final HashSet<String> waMembers = filterCache.getWaMembers();

        // Query global cache, set local cache to it if what we search was found.
        if (waMembers != null) {
            nations = waMembers;
            return;
        }

        // If global cache does not contain what we need, do an API call to
        // retrieve the data, then store it in global cache and local cache.
        final WorldAssembly wa = nationStates.getWorldAssembly(Council.SECURITY_COUNCIL).shards(WorldAssemblyShard.MEMBERS).execute();
        filterCache.setWaMembers(new HashSet<>(wa.members));
        nations = filterCache.getWaMembers();
        cantRetrieveMoreNations = true;
    }
}
