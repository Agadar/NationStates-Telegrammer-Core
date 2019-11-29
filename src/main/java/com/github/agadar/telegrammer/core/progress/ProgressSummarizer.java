package com.github.agadar.telegrammer.core.progress;

import java.util.Collection;
import java.util.HashSet;

import com.github.agadar.telegrammer.core.misc.SkippedRecipientReason;

import lombok.NonNull;

/**
 * Summarizes telegram queuing progress, containing logic to properly keep track
 * of the progress.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class ProgressSummarizer {

    private final Collection<String> queuedSucces = new HashSet<>();
    private final Collection<String> recipientDidntExist = new HashSet<>();
    private final Collection<String> recipientIsBlocking = new HashSet<>();
    private final Collection<String> disconnectOrOtherReason = new HashSet<>();

    /**
     * Registers the successful queuing of a telegram to the recipient.
     * 
     * @param recipient The recipient to whom a telegram was successfully queued.
     */
    public void registerSucces(@NonNull String recipient) {
        queuedSucces.add(recipient);
        recipientDidntExist.remove(recipient);
        recipientIsBlocking.remove(recipient);
        disconnectOrOtherReason.remove(recipient);
    }

    /**
     * Registers the failed queuing of a telegram to the recipient.
     * 
     * @param recipient The recipient to whom a telegram failed to be queued.
     * @param reason    The reason why queuing failed.
     */
    public void registerFailure(@NonNull String recipient, SkippedRecipientReason reason) {

        if (queuedSucces.contains(recipient)) {
            return;
        }

        if (reason == null) {
            disconnectOrOtherReason.add(recipient);
            return;
        }

        switch (reason) {
            case BLOCKING_RECRUITMENT:
            case BLOCKING_CAMPAIGN:
                recipientIsBlocking.add(recipient);
                break;
            case NOT_FOUND:
                recipientDidntExist.add(recipient);
                break;
            default:
                break;
        }
    }

    /**
     * Gets a summary of the current telegram queuing progress.
     * 
     * @return A summary of the current telegram queuing progress.
     */
    public ProgressSummary getProgressSummary() {
        return ProgressSummary.builder()
                .disconnectOrOtherReason(disconnectOrOtherReason.size())
                .queuedSucces(queuedSucces.size())
                .recipientDidntExist(recipientDidntExist.size())
                .recipientIsBlocking(recipientIsBlocking.size())
                .build();
    }
}
