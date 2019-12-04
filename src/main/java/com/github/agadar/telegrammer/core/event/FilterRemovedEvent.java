package com.github.agadar.telegrammer.core.event;

import java.util.EventObject;
import java.util.List;

import com.github.agadar.telegrammer.core.Telegrammer;
import lombok.Getter;

/**
 * An event fired by {@link Telegrammer} when a filter has been removed.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class FilterRemovedEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    /**
     * The resulting filters after removing one or more.
     */
    @Getter
    private final List<String> filters;

    /**
     * The number of recipients.
     */
    @Getter
    private final int numberOfRecipients;

    public FilterRemovedEvent(Object source, List<String> filters, int numberOfRecipients) {
        super(source);
        this.filters = filters;
        this.numberOfRecipients = numberOfRecipients;
    }

}
