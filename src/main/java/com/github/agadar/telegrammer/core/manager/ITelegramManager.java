package com.github.agadar.telegrammer.core.manager;

import com.github.agadar.telegrammer.core.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.filter.abstractfilter.Filter;

import java.util.Set;

/**
 * Manages the recipients list and sending telegrams to the former.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface ITelegramManager {

    /**
     * Returns true if none of the filters can retrieve any new nations by more
     * calls to refresh().
     *
     * @return
     */
    public boolean cantRetrieveMoreNations();

    /**
     * Returns true if at least one of the filters is potentially infinite.
     *
     * @return
     */
    public boolean potentiallyInfinite();

    /**
     * Adds new filter. Assumes it hasn't been refreshed yet.
     *
     * @param filter
     */
    public void addFilter(Filter filter);

    /**
     * Gives the number of recipients.
     *
     * @return
     */
    public int numberOfRecipients();

    /**
     * Gets a copy of the Recipients.
     *
     * @return
     */
    public Set<String> getRecipients();

    /**
     * Resets and reapplies all filters to the address list.
     */
    public void resetAndReapplyFilters();

    /**
     * Refreshes and reapplies all filters to the address list.
     */
    public void refreshAndReapplyFilters();

    /**
     * Removes the filter with the given index.
     *
     * @param index
     */
    public void removeFilterAt(int index);

    /**
     * Starts sending the telegram to the recipients.
     *
     * @param nonblocking If true, then the telegrams will be sent in a new
     * thread.
     * @throws IllegalArgumentException If the variables are not properly set.
     */
    public void startSending(boolean nonblocking);

    /**
     * Stops sending the telegram to the recipients. Does nothing if
     * startSending
     */
    public void stopSending();

    /**
     * Registers new telegram manager listeners.
     *
     * @param newlisteners the listeners to register
     */
    public void addListeners(TelegramManagerListener... newlisteners);
}
