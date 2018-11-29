package com.github.agadar.telegrammer.core.telegram.event;

import java.util.EventObject;

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
     * True if the manager stopped as result of an error.
     */
    private final boolean causedByError;
    /**
     * Contains an error message if stopped as result of an error.
     */
    private final String errorMsg;
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

    public StoppedSendingEvent(Object source, boolean causedByError, String errorMsg, int queuedSucces,
            int recipientDidntExist, int recipientIsBlocking, int disconnectOrOtherReason) {
        super(source);
        this.causedByError = causedByError;
        this.errorMsg = errorMsg;
        this.queuedSucces = queuedSucces;
        this.recipientDidntExist = recipientDidntExist;
        this.recipientIsBlocking = recipientIsBlocking;
        this.disconnectOrOtherReason = disconnectOrOtherReason;
    }
}
