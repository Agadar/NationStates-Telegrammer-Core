package com.github.agadar.telegrammer.core.sender;

import java.util.Collection;
import java.util.function.Predicate;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.nationstates.event.TelegramSentListener;
import com.github.agadar.nationstates.shard.NationShard;
import com.github.agadar.telegrammer.core.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.event.RecipientsRefreshedEvent;
import com.github.agadar.telegrammer.core.event.StoppedSendingEvent;
import com.github.agadar.telegrammer.core.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.history.TelegramHistory;
import com.github.agadar.telegrammer.core.misc.SkippedRecipientReason;
import com.github.agadar.telegrammer.core.misc.TelegramType;
import com.github.agadar.telegrammer.core.progress.ProgressSummarizer;
import com.github.agadar.telegrammer.core.progress.ProgressSummary;
import com.github.agadar.telegrammer.core.settings.TelegrammerSettings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Runnable used by TelegramManager which sends the telegrams to the recipients.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Slf4j
@RequiredArgsConstructor
public class SendTelegramsRunnable implements Runnable, TelegramSentListener {

    private final ProgressSummarizer progressSummarizer = new ProgressSummarizer();
    private final Collection<TelegramManagerListener> listeners;
    private final int noRecipientsFoundTimeOut;
    private final NationStates nationStates;
    private final TelegramHistory historyManager;
    private final TelegrammerSettings settings;

    private String[] recipients;

    @Override
    public void run() {
        try {
            do {
                // Just get the recipients, as we expect the API to already have been called
                // before running.
                recipients = getRecipients();

                if (recipients.length > 0) {
                    performTelegramSendingBehavior();
                } else {
                    performNoRecipientsFoundBehavior();
                }

            } while (!Thread.currentThread().isInterrupted() && settings.getRunIndefinitely());

        } catch (InterruptedException ex) {
            /* Just fall through to finally. */

        } finally {
            var stoppedEvent = createStoppedEvent();
            logStoppedEvent(stoppedEvent);
            listeners.stream().forEach((tsl) -> {
                tsl.handleStoppedSending(stoppedEvent);
            });
        }
    }

    @Override
    public void handleTelegramSent(TelegramSentEvent event) {
        // Update the History. We're assuming removeOldRecipients is always
        // called before this and the Telegram Id didn't change in the meantime,
        // so there is no need to make sure the entry for the current Telegram Id
        // changed.
        event.getException().ifPresentOrElse(exception -> {
            progressSummarizer.registerFailure(event.getRecipient(), null);
        }, () -> {
            historyManager.saveHistory(settings.getTelegramId(), event.getRecipient(),
                    SkippedRecipientReason.PREVIOUS_RECIPIENT);
            progressSummarizer.registerSucces(event.getRecipient());
        });

        synchronized (listeners) {
            listeners.stream().forEach((tsl) -> {
                tsl.handleTelegramSent(event);
            });
        }
    }

    /**
     * Gets a summary of the current telegram queuing progress.
     * 
     * @return A summary of the current telegram queuing progress.
     */
    public ProgressSummary getProgressSummary() {
        return progressSummarizer.getProgressSummary();
    }

    private void performTelegramSendingBehavior() {
        if (settings.getTelegramType() == TelegramType.NORMAL
                || settings.getTelegramType() == null) {
            sendNormalTelegram();
        } else {
            sendSpecialTelegram();
        }
    }

    private void sendNormalTelegram() {
        if (settings.getUpdateAfterEveryTelegram()) {
            sendNormalTelegramsAndUpdateRecipientsAfterEveryTelegram();
        } else {
            sendTelegram(recipients);
        }
    }

    private void sendNormalTelegramsAndUpdateRecipientsAfterEveryTelegram() {
        while (recipients.length > 0 && !Thread.currentThread().isInterrupted()) {
            sendTelegram(recipients[0]);

            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            updateRecipientsFromApi();
            recipients = getRecipients();
        }
    }

    private void sendSpecialTelegram() {
        var canReceivePredicate = getCanReceiveTelegramPredicate();

        if (settings.getUpdateAfterEveryTelegram()) {
            sendSpecialTelegramsAndUpdateRecipientsAfterEveryTelegram(canReceivePredicate);
        } else {
            sendSpecialTelegram(canReceivePredicate);
        }
    }

    private void sendSpecialTelegramsAndUpdateRecipientsAfterEveryTelegram(Predicate<String> canReceivePredicate) {
        while (recipients.length > 0 && !Thread.currentThread().isInterrupted()) {
            int index = getIndexOfNextRecipientThatCanReceive(canReceivePredicate, 0);

            if (Thread.currentThread().isInterrupted() || index == -1) {
                break;
            }
            sendTelegram(recipients[index]);

            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            updateRecipientsFromApi();
            recipients = getRecipients();
        }
    }

    private void sendSpecialTelegram(Predicate<String> canReceivePredicate) {
        int index = 0;
        while ((index = getIndexOfNextRecipientThatCanReceive(canReceivePredicate, index)) != -1) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            sendTelegram(recipients[index++]);
        }
    }

    private void performNoRecipientsFoundBehavior() throws InterruptedException {
        var event = new NoRecipientsFoundEvent(this, noRecipientsFoundTimeOut);
        log.info("No recipients were found. Sleeping for {} seconds before refreshing recipients again",
                event.getTimeOut() / 1000);

        synchronized (listeners) {
            listeners.stream().forEach((tsl) -> {
                tsl.handleNoRecipientsFound(event);
            });
        }
        if (settings.getRunIndefinitely()) {
            Thread.sleep(noRecipientsFoundTimeOut);
            updateRecipientsFromApi();
        }
    }

    /**
     * Gets the recipients, applying the filters and the history but not calling the
     * API.
     * 
     * @return
     */
    private String[] getRecipients() {
        var recipients = settings.getFilters().getRecipients(settings.getTelegramId());
        return recipients.toArray(new String[recipients.size()]);
    }

    /**
     * Updates the recipients from the API.
     */
    private void updateRecipientsFromApi() {
        var failedFilters = settings.getFilters().refreshFilters();

        if (failedFilters.isEmpty()) {
            log.info("Refreshed filters without failures");
        } else {
            log.warn("Failures occured while refreshing filters. Check error logs");
        }
        var refrevent = new RecipientsRefreshedEvent(this, failedFilters);

        synchronized (listeners) {
            listeners.stream().forEach((tsl) -> {
                tsl.handleRecipientsRefreshed(refrevent);
            });
        }
    }

    /**
     * Sends the telegram to the specified recipient(s).
     *
     * @param recipients
     */
    private void sendTelegram(String... recipients) {
        var q = nationStates.sendTelegrams(settings.getClientKey(), settings.getTelegramId(),
                settings.getSecretKey(), recipients).addListeners(this);

        if (settings.getTelegramType() == TelegramType.RECRUITMENT) {
            q.sendAsRecruitmentTelegram();
        }
        q.execute(null);
    }

    /**
     * Returns whether or not the recipient may receive a recruitment telegram. If
     * not, removes it from Recipients and throws a RecipientRemovedEvent. If the
     * server couldn't be reached, always returns true.
     *
     * @param recipient
     * @return
     */
    private boolean canReceiveRecruitmentTelegrams(String recipient) {
        try {
            var n = nationStates.getNation(recipient).shards(NationShard.CAN_RECEIVE_RECRUITMENT_TELEGRAMS)
                    .canReceiveTelegramFromRegion(settings.getFromRegion()).execute();
            var reason = (n == null) ? SkippedRecipientReason.NOT_FOUND
                    : !n.isCanReceiveRecruitmentTelegrams() ? SkippedRecipientReason.BLOCKING_RECRUITMENT : null;
            return canReceiveXTelegrams(reason, recipient);
        } catch (Exception ex) {
            log.error("An error occured while checking whether a recipient can receive the telegram", ex);
            return true;
        }
    }

    /**
     * Returns whether or not the recipient may receive a campaign telegram. If not,
     * removes it from Recipients and throws a RecipientRemovedEvent. If the server
     * couldn't be reached, always returns true.
     *
     * @param recipient
     * @return
     */
    private boolean canReceiveCampaignTelegrams(String recipient) {
        try {
            var n = nationStates.getNation(recipient).shards(NationShard.CAN_RECEIVE_CAMPAIGN_TELEGRAMS)
                    .canReceiveTelegramFromRegion(settings.getFromRegion()).execute();
            var reason = (n == null) ? SkippedRecipientReason.NOT_FOUND
                    : !n.isCanReceiveCampaignTelegrams() ? SkippedRecipientReason.BLOCKING_CAMPAIGN : null;
            return canReceiveXTelegrams(reason, recipient);
        } catch (Exception ex) {
            log.error("An error occured while checking whether a recipient can receive the telegram", ex);
            return true;
        }
    }

    /**
     * Shared behavior by canReceiveRecruitmentTelegrams(...) and
     * canReceiveCampaignTelegrams(...).
     *
     * @param reason
     * @param recipient
     * @return
     */
    private boolean canReceiveXTelegrams(SkippedRecipientReason reason, String recipient) {
        if (reason != null) {
            progressSummarizer.registerFailure(recipient, reason);
            historyManager.saveHistory(settings.getTelegramId(), recipient, reason);
            var event = new RecipientRemovedEvent(this, recipient, reason);
            log.info("Skipping recipient '{}' for the following reason: {}", recipient, reason);

            synchronized (listeners) {
                listeners.stream().forEach((tsl) -> {
                    tsl.handleRecipientRemoved(event);
                });
            }
            return false;
        }
        return true;
    }

    /**
     * Depending on a specified TelegramType, returns the corresponding
     * canReceiveXTelegram checker.
     * 
     * @return
     */
    private Predicate<String> getCanReceiveTelegramPredicate() {
        switch (settings.getTelegramType()) {
            case RECRUITMENT:
                return this::canReceiveRecruitmentTelegrams;
            case CAMPAIGN:
                return this::canReceiveCampaignTelegrams;
            default:
                return recipient -> true;
        }
    }

    /**
     * Gets the index of the next recipient in recipientsArray that can receive a
     * telegram.
     * 
     * @param canReceiveTelegramPredicate
     * @param startingIndex
     * @return -1 If none was found or the thread was interrupted, otherwise an
     *         index < recipientsArray length.
     */
    private int getIndexOfNextRecipientThatCanReceive(Predicate<String> canReceiveTelegramPredicate,
            int startingIndex) {

        if (startingIndex >= recipients.length || Thread.currentThread().isInterrupted()) {
            return -1;
        }

        while (!canReceiveTelegramPredicate.test(recipients[startingIndex])) {
            startingIndex++;

            if (startingIndex >= recipients.length || Thread.currentThread().isInterrupted()) {
                return -1;
            }
        }
        return startingIndex;
    }

    private StoppedSendingEvent createStoppedEvent() {
        return new StoppedSendingEvent(this, progressSummarizer.getProgressSummary());
    }

    private void logStoppedEvent(StoppedSendingEvent stoppedEvent) {
        log.info(
                "Stopped queueing telegrams. Queued: {}; blocked by category: {}; recipients not found: {}; failed: {}",
                stoppedEvent.getQueuedSucces(), stoppedEvent.getRecipientIsBlocking(),
                stoppedEvent.getRecipientDidntExist(), stoppedEvent.getDisconnectOrOtherReason());
    }
}
