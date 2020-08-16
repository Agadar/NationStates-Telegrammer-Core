package com.github.agadar.telegrammer.core.progress;

import java.util.Collection;
import java.util.HashSet;

import com.github.agadar.nationstates.exception.NationStatesResourceNotFoundException;
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
        clearRecipientFromCollections(recipient);
        queuedSucces.add(recipient);
    }
    
    /**
     * Registers the failed queuing of a telegram to the recipient.
     * 
     * @param recipient The recipient to whom a telegram failed to be queued.
     * @param reason    The reason why queuing failed.
     */
    public void registerFailure(@NonNull String recipient, @NonNull SkippedRecipientReason reason) {
        clearRecipientFromCollections(recipient);
        
        switch (reason) {
            case BLOCKING_RECRUITMENT:
            case BLOCKING_CAMPAIGN:
                recipientIsBlocking.add(recipient);
                break;
            case NOT_FOUND:
                recipientDidntExist.add(recipient);
                break;
            default:
                disconnectOrOtherReason.add(recipient);
                break;
        }
    }
    
    /**
     * Registers the failed queuing of a telegram to the recipient.
     * 
     * @param recipient The recipient to whom a telegram failed to be queued.
     * @param reason    The exception that caused the failure.
     * @return          The reason why the telegram failed.
     */
    public SkippedRecipientReason registerFailure(@NonNull String recipient, @NonNull Exception exception) {
        clearRecipientFromCollections(recipient);
        
        if (exception instanceof NationStatesResourceNotFoundException) {
            recipientDidntExist.add(recipient);
            return SkippedRecipientReason.NOT_FOUND;
        }
        disconnectOrOtherReason.add(recipient);
        return SkippedRecipientReason.ERROR;
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
    
    private void clearRecipientFromCollections(String recipient) {
        queuedSucces.remove(recipient);
        recipientDidntExist.remove(recipient);
        recipientIsBlocking.remove(recipient);
        disconnectOrOtherReason.remove(recipient);
    }
}
