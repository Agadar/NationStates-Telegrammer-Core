package com.github.agadar.telegrammer.core.runnable;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.nationstates.event.TelegramSentListener;
import com.github.agadar.nationstates.shard.NationShard;
import com.github.agadar.telegrammer.core.TelegrammerListener;
import static com.github.agadar.telegrammer.core.settings.CoreSettingKey.*;
import com.github.agadar.telegrammer.core.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.event.StartedRefreshingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.FinishedRefreshingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.StoppedSendingEvent;
import com.github.agadar.telegrammer.core.history.TelegramHistory;
import com.github.agadar.telegrammer.core.misc.SkippedRecipientReason;
import com.github.agadar.telegrammer.core.misc.TelegramType;
import com.github.agadar.telegrammer.core.misc.TelegrammerState;
import com.github.agadar.telegrammer.core.progress.ProgressSummarizer;
import com.github.agadar.telegrammer.core.progress.ProgressSummary;
import com.github.agadar.telegrammer.core.recipients.listbuilder.RecipientsListBuilder;
import com.github.agadar.telegrammer.core.settings.Settings;

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
    private final Collection<TelegrammerListener> listeners;
    private final int noRecipientsFoundTimeOut;
    private final NationStates nationStates;
    private final TelegramHistory historyManager;
    private final Settings settings;

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

            } while (!Thread.currentThread().isInterrupted()
                    && settings.getValue(RUN_INDEFINITELY.getKey(), Boolean.class));

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
            String telegramId = settings.getValue(TELEGRAM_ID.getKey(), String.class);
            historyManager.saveHistory(telegramId, event.getRecipient(), SkippedRecipientReason.PREVIOUS_RECIPIENT);
            progressSummarizer.registerSucces(event.getRecipient());
        });

        listeners.stream().forEach((tsl) -> {
            tsl.handleTelegramSent(event);
        });

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
        var telegramType = settings.getValue(TELEGRAM_TYPE.getKey(), TelegramType.class);
        if (telegramType == TelegramType.NORMAL) {
            sendNormalTelegram();
        } else {
            sendSpecialTelegram();
        }
    }

    private void sendNormalTelegram() {
        if (settings.getValue(UPDATE_AFTER_EVERY_TELEGRAM.getKey(), Boolean.class)) {
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

        if (settings.getValue(UPDATE_AFTER_EVERY_TELEGRAM.getKey(), Boolean.class)) {
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

        listeners.stream().forEach((tsl) -> {
            tsl.handleNoRecipientsFound(event);
        });

        if (settings.getValue(RUN_INDEFINITELY.getKey(), Boolean.class)) {
            Thread.sleep(noRecipientsFoundTimeOut);
            updateRecipientsFromApi();
        }
    }

    private String[] getRecipients() {
        var filters = settings.getValue(FILTERS.getKey(), RecipientsListBuilder.class);
        String telegramId = settings.getValue(TELEGRAM_ID.getKey(), String.class);
        var recipients = filters.getRecipients(telegramId);
        return recipients.toArray(new String[recipients.size()]);
    }

    private void updateRecipientsFromApi() {
        var startEvent = new StartedRefreshingRecipientsEvent(this, TelegrammerState.QUEUING_TELEGRAMS);
        listeners.stream().forEach((tsl) -> {
            tsl.handleStartedRefreshingRecipients(startEvent);
        });
        var filters = settings.getValue(FILTERS.getKey(), RecipientsListBuilder.class);
        var failedFilters = filters.refreshFilters();
        int numberOfRecipients = filters.getRecipients(settings.getValue(TELEGRAM_ID.getKey(), String.class)).size();
        var refrevent = new FinishedRefreshingRecipientsEvent(this, TelegrammerState.QUEUING_TELEGRAMS, failedFilters,
                numberOfRecipients, stringifyFilters());
        listeners.stream().forEach((tsl) -> {
            tsl.handleFinishedRefreshingRecipients(refrevent);
        });
    }

    private void sendTelegram(String... recipients) {
        String clientKey = settings.getValue(CLIENT_KEY.getKey(), String.class);
        String telegramId = settings.getValue(TELEGRAM_ID.getKey(), String.class);
        String secretKey = settings.getValue(SECRET_KEY.getKey(), String.class);
        var telegramType = settings.getValue(TELEGRAM_TYPE.getKey(), TelegramType.class);
        var query = nationStates.sendTelegrams(clientKey, telegramId, secretKey, recipients).addListeners(this);
        if (telegramType == TelegramType.RECRUITMENT) {
            query.sendAsRecruitmentTelegram();
        }
        query.execute(null);
    }

    private boolean canReceiveRecruitmentTelegrams(String recipient) {
        try {
            var n = nationStates.getNation(recipient).shards(NationShard.CAN_RECEIVE_RECRUITMENT_TELEGRAMS)
                    .canReceiveTelegramFromRegion(settings.getValue(FROM_REGION.getKey(), String.class)).execute();
            var reason = (n == null) ? SkippedRecipientReason.NOT_FOUND
                    : !n.isCanReceiveRecruitmentTelegrams() ? SkippedRecipientReason.BLOCKING_RECRUITMENT : null;
            return canReceiveXTelegrams(reason, recipient);
        } catch (Exception ex) {
            log.error("An error occured while checking whether a recipient can receive the telegram", ex);
            return true;
        }
    }

    private boolean canReceiveCampaignTelegrams(String recipient) {
        try {
            var n = nationStates.getNation(recipient).shards(NationShard.CAN_RECEIVE_CAMPAIGN_TELEGRAMS)
                    .canReceiveTelegramFromRegion(settings.getValue(FROM_REGION.getKey(), String.class)).execute();
            var reason = (n == null) ? SkippedRecipientReason.NOT_FOUND
                    : !n.isCanReceiveCampaignTelegrams() ? SkippedRecipientReason.BLOCKING_CAMPAIGN : null;
            return canReceiveXTelegrams(reason, recipient);
        } catch (Exception ex) {
            log.error("An error occured while checking whether a recipient can receive the telegram", ex);
            return true;
        }
    }

    private boolean canReceiveXTelegrams(SkippedRecipientReason reason, String recipient) {
        if (reason != null) {
            progressSummarizer.registerFailure(recipient, reason);
            historyManager.saveHistory(settings.getValue(TELEGRAM_ID.getKey(), String.class), recipient, reason);
            var event = new RecipientRemovedEvent(this, recipient, reason);
            log.info("Skipping recipient '{}' for the following reason: {}", recipient, reason);

            listeners.stream().forEach((tsl) -> {
                tsl.handleRecipientRemoved(event);
            });
            return false;
        }
        return true;
    }

    private Predicate<String> getCanReceiveTelegramPredicate() {
        switch (settings.getValue(TELEGRAM_TYPE.getKey(), TelegramType.class)) {
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

    private List<String> stringifyFilters() {
        return settings.getValue(FILTERS.getKey(), RecipientsListBuilder.class).getFilters().stream()
                .map(filter -> filter.toString())
                .collect(Collectors.toList());
    }
}
