package com.github.agadar.telegrammer.core.event;

import java.util.EventObject;
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
     * The index of the filter that has been removed.
     */
    @Getter
    private final int index;

    public FilterRemovedEvent(Object source, int index) {
        super(source);
        this.index = index;
    }

}
