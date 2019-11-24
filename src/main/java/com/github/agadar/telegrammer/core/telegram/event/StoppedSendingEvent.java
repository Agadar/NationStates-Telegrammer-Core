package com.github.agadar.telegrammer.core.telegram.event;

import java.util.EventObject;

import com.github.agadar.telegrammer.core.telegram.progress.ProgressSummary;

import lombok.Getter;

/**
 * Fired by TelegramManager when it's stopped sending telegrams.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Getter
public class StoppedSendingEvent extends EventObject {

    private static final long serialVersionUID = 1L;

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

    /**
     * Constructor.
     * 
     * @param source          The component that fired this event.
     * @param progressSummary The (final) telegram queuing progress.
     */
    public StoppedSendingEvent(Object source, ProgressSummary progressSummary) {
        super(source);
        this.queuedSucces = progressSummary.getQueuedSucces();
        this.recipientDidntExist = progressSummary.getRecipientDidntExist();
        this.recipientIsBlocking = progressSummary.getRecipientIsBlocking();
        this.disconnectOrOtherReason = progressSummary.getDisconnectOrOtherReason();
    }
}
