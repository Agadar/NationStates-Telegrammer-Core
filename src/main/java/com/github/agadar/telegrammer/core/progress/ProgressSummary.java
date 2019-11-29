package com.github.agadar.telegrammer.core.progress;

import lombok.Builder;
import lombok.Getter;

/**
 * Summary of the current telegram queuing progress.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
@Getter
@Builder
public class ProgressSummary {
    /**
     * Number of telegrams queued successfully.
     */
    private final int queuedSucces;
    /**
     * Number of telegrams that failed to queue because the recipient didn't exist.
     */
    private final int recipientDidntExist;
    /**
     * Number of telegrams that failed to queue because the recipient is blocking
     * telegrams of that type.
     */
    private final int recipientIsBlocking;
    /**
     * Number of telegrams that failed to queue because of some other reason, such
     * as disconnect.
     */
    private final int disconnectOrOtherReason;
}
