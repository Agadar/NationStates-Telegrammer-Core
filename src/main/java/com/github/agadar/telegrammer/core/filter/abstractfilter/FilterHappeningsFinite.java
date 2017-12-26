package com.github.agadar.telegrammer.core.filter.abstractfilter;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.telegrammer.core.manager.IHistoryManager;
import com.github.agadar.telegrammer.core.util.IFilterCache;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Special type of FilterHappenings that CAN stop retrieving new recipients at
 * some point, because it is limited by a maximum number.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class FilterHappeningsFinite extends FilterHappenings {

    /**
     * The original amount of nations to retrieve.
     */
    final int orgAmountToRetrieve;

    /**
     * The amount of nations left to retrieve.
     */
    int amountToRetrieve;

    public FilterHappeningsFinite(INationStates nationStates, IHistoryManager historyManager, IFilterCache filterCache, KeyWord keyWord, int amountToRetrieve) {
        super(nationStates, historyManager, filterCache, keyWord);
        this.amountToRetrieve = Math.max(0, amountToRetrieve);
        this.orgAmountToRetrieve = amountToRetrieve;
    }

    @Override
    public void applyFilter(Set<String> addresses) {
        final HashSet<String> copy = new HashSet<>(nations);
        historyManager.removeOldRecipients(copy);
        int diff = amountToRetrieve - copy.size();
        amountToRetrieve = Math.max(0, diff);

        // If amountToRetrieve is negative, trim the set with the difference.
        for (Iterator<String> it = copy.iterator(); it.hasNext() && diff < 0; diff++) {
            it.next();
            it.remove();
        }
        addresses.addAll(copy);
    }

    @Override
    public boolean cantRetrieveMoreNations() {
        return amountToRetrieve < 1;
    }

    @Override
    public void reset() {
        amountToRetrieve = orgAmountToRetrieve;
    }
}
