package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.enumerator.RegionTag;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterRegionByTags;
import com.github.agadar.telegrammer.core.manager.IHistoryManager;
import com.github.agadar.telegrammer.core.util.IFilterCache;

import java.util.Set;

/**
 * Filter for adding/removing nations that are in regions without the supplied
 * tags.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterRegionsWithoutTags extends FilterRegionByTags {

    public FilterRegionsWithoutTags(INationStates nationStates, IHistoryManager historyManager, IFilterCache filterCache, Set<RegionTag> tags, boolean add) {
        super(nationStates, historyManager, filterCache, tags, add);
    }

    @Override
    protected World getWorld() {
        return nationStates.getWorld(WorldShard.REGIONS_BY_TAG).regionsWithoutTags(tags
                .toArray(new RegionTag[tags.size()])).execute();
    }
}
