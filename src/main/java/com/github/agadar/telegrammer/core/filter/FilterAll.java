package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.Filter;
import com.github.agadar.telegrammer.core.manager.IHistoryManager;
import com.github.agadar.telegrammer.core.util.IFilterCache;

import java.util.HashSet;

/**
 * Filter for retrieving ALL nations.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterAll extends Filter {

    public FilterAll(INationStates nationStates, IHistoryManager historyManager, IFilterCache filterCache) {
        super(nationStates, historyManager, filterCache);
    }

    @Override
    public void refresh() {
        // If we already retrieved data before, just return.
        if (nations != null) {
            return;
        }
        final HashSet<String> allNations = filterCache.getAllNations();

        // Query global cache, set local cache to it if what we search was found.
        if (allNations != null) {
            nations = allNations;
            return;
        }

        // If global cache does not contain what we need, do an API call to
        // retrieve the data, then store it in global cache and local cache.
        final World w = nationStates.getWorld(WorldShard.NATIONS).execute();
        filterCache.setAllNations(new HashSet<>(w.nations));
        nations = filterCache.getAllNations();
        cantRetrieveMoreNations = true;
    }
}
