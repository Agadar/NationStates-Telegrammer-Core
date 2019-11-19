package com.github.agadar.telegrammer.core.telegram.sender;

import java.util.Collection;
import java.util.function.Predicate;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.nationstates.event.TelegramSentListener;
import com.github.agadar.nationstates.shard.NationShard;
import com.github.agadar.telegrammer.core.properties.ApplicationProperties;
import com.github.agadar.telegrammer.core.recipients.listbuilder.RecipientsListBuilder;
import com.github.agadar.telegrammer.core.telegram.QueuedStats;
import com.github.agadar.telegrammer.core.telegram.SkippedRecipientReason;
import com.github.agadar.telegrammer.core.telegram.TelegramType;
import com.github.agadar.telegrammer.core.telegram.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.telegram.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.telegram.event.RecipientsRefreshedEvent;
import com.github.agadar.telegrammer.core.telegram.event.StoppedSendingEvent;
import com.github.agadar.telegrammer.core.telegram.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.telegram.history.TelegramHistory;

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

    private final QueuedStats queuedStats = new QueuedStats();
    private final RecipientsListBuilder recipientsListBuilder;
    private final Collection<TelegramManagerListener> listeners;
    private final int noRecipientsFoundTimeOut;
    private final NationStates nationStates;
    private final TelegramHistory historyManager;
    private final ApplicationProperties properties;

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

            } while (!Thread.currentThread().isInterrupted() && properties.isRunIndefinitely());

        } catch (InterruptedException ex) {
            /* Just fall through to finally. */

        } finally {
            var stoppedEvent = new StoppedSendingEvent(this, false, null,
                    queuedStats.getQueuedSucces(), queuedStats.getRecipientDidntExist(),
                    queuedStats.getRecipientIsBlocking(), queuedStats.getDisconnectOrOtherReason());
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
            queuedStats.registerFailure(event.getRecipient(), null);
        }, () -> {
            historyManager.saveHistory(properties.getTelegramId(), event.getRecipient(),
                    SkippedRecipientReason.PREVIOUS_RECIPIENT);
            queuedStats.registerSucces(event.getRecipient());
        });

        synchronized (listeners) {
            listeners.stream().forEach((tsl) -> {
                tsl.handleTelegramSent(event);
            });
        }
    }

    private void performTelegramSendingBehavior() {
        if (properties.getLastTelegramType() == TelegramType.NORMAL || properties.getLastTelegramType() == null) {
            sendNormalTelegram();
        } else {
            sendSpecialTelegram();
        }
    }

    private void sendNormalTelegram() {
        if (properties.isUpdateRecipientsAfterEveryTelegram()) {
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

        if (properties.isUpdateRecipientsAfterEveryTelegram()) {
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
        synchronized (listeners) {
            listeners.stream().forEach((tsl) -> {
                tsl.handleNoRecipientsFound(event);
            });
        }
        if (properties.isRunIndefinitely()) {
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
        var recipients = recipientsListBuilder.getRecipients(properties.getTelegramId());
        return recipients.toArray(new String[recipients.size()]);
    }

    /**
     * Updates the recipients from the API.
     */
    private void updateRecipientsFromApi() {
        var failedFilters = recipientsListBuilder.refreshFilters();
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
        var q = nationStates.sendTelegrams(properties.getClientKey(), properties.getTelegramId(),
                properties.getSecretKey(), recipients).addListeners(this);

        if (properties.getLastTelegramType() == TelegramType.RECRUITMENT) {
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
                    .canReceiveTelegramFromRegion(properties.getFromRegion()).execute();
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
                    .canReceiveTelegramFromRegion(properties.getFromRegion()).execute();
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
            queuedStats.registerFailure(recipient, reason);
            historyManager.saveHistory(properties.getTelegramId(), recipient, reason);
            var event = new RecipientRemovedEvent(this, recipient, reason);

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
        switch (properties.getLastTelegramType()) {
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
}
