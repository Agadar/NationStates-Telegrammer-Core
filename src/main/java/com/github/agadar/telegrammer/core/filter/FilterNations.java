package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterAddOrRemove;
import com.github.agadar.telegrammer.core.manager.IHistoryManager;
import com.github.agadar.telegrammer.core.util.IFilterCache;

import java.util.Set;

/**
 * Filter for adding/removing individual nations from address list.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterNations extends FilterAddOrRemove {

    public FilterNations(INationStates nationStates, IHistoryManager historyManager, IFilterCache filterCache, Set<String> nations, boolean add) {
        super(nationStates, historyManager, filterCache, add);
        this.nations = nations;
    }
}
