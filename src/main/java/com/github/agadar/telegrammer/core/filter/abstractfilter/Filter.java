package com.github.agadar.telegrammer.core.filter.abstractfilter;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.telegrammer.core.manager.IHistoryManager;
import com.github.agadar.telegrammer.core.util.IFilterCache;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract parent for all filters.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class Filter {

    /**
     * The wrapper used for consuming the official API.
     */
    protected final INationStates nationStates;

    /**
     * The cache that is to be used by all child filters.
     */
    protected final IFilterCache filterCache;

    protected final IHistoryManager historyManager;

    /**
     * Set containing the nations from the last time retrieveNations() was
     * called.
     */
    protected Set<String> nations;

    /**
     * Whether or not this filter can not retrieve any new nations by more calls
     * to refresh().
     */
    protected boolean cantRetrieveMoreNations = false;

    public Filter(INationStates nationStates, IHistoryManager historyManager, IFilterCache filterCache) {
        this.nationStates = nationStates;
        this.historyManager = historyManager;
        this.filterCache = filterCache;
    }

    /**
     * Applies this filter to the supplied set of nations, removing or adding
     * nations according to this filter's rules.
     *
     * refresh() should've been called at least once before calling this.
     *
     * @param addresses
     */
    public void applyFilter(Set<String> addresses) {
        final Set<String> copy = new HashSet<>(nations);
        historyManager.removeOldRecipients(copy);
        addresses.addAll(copy);
    }

    /**
     * Refreshes this filter. Depending on the implementation, this may mean: -
     * Doing nothing; - Caching data from the global cache; - Caching data from
     * a daily dump file; - Caching data from the server.
     */
    public void refresh() {
        cantRetrieveMoreNations = true;
    }

    /**
     * Whether or not this filter can not retrieve any new nations anymore by
     * subsequent calls to refresh().
     *
     * @return
     */
    public boolean cantRetrieveMoreNations() {
        return cantRetrieveMoreNations;
    }

    /**
     * Resets this filter. Does nothing by default. Child overrides might make
     * it so that this filter can retrieve more nations again.
     */
    public void reset() {
    }

    /**
     * Returns whether or not this filter can potentially keep supplying new
     * recipients forever.
     *
     * @return
     */
    public boolean potentiallyInfinite() {
        return false;
    }

    /**
     * Returns a copy of the nations selected by this filter. Used for debugging
     * purposes.
     *
     * @return a copy of the nations selected by this filter.
     */
    public Set<String> getNations() {
        return new HashSet<>(nations);
    }
}
