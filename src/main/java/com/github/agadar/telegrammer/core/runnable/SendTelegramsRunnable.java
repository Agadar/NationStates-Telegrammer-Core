package com.github.agadar.telegrammer.core.runnable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.nation.Nation;
import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.nationstates.event.TelegramSentListener;
import com.github.agadar.nationstates.exception.NationStatesResourceNotFoundException;
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

import lombok.extern.slf4j.Slf4j;

/**
 * Runnable used by TelegramManager which sends the telegrams to the recipients.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Slf4j
public class SendTelegramsRunnable implements Runnable, TelegramSentListener {

    private final ProgressSummarizer progressSummarizer;
    private final Collection<TelegrammerListener> listeners;
    private final int noRecipientsFoundTimeOut;
    private final NationStates nationStates;
    private final TelegramHistory historyManager;
    private final Settings settings;
    private final Predicate<String> canReceiveTelegramPredicate;

    private String[] recipients;

    public SendTelegramsRunnable(Collection<TelegrammerListener> listeners, int noAddresseesFoundTimeout,
            NationStates nationStates, TelegramHistory historyManager, Settings settings) {

        this.listeners = listeners;
        this.noRecipientsFoundTimeOut = noAddresseesFoundTimeout;
        this.nationStates = nationStates;
        this.historyManager = historyManager;
        this.settings = settings;
        progressSummarizer = new ProgressSummarizer();
        canReceiveTelegramPredicate = getCanReceiveTelegramPredicate();
    }

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

        SkippedRecipientReason skippedReason;

        if (event.getException().isPresent()) {
            skippedReason = progressSummarizer.registerFailure(event.getRecipient(), event.getException().get());
        } else {
            skippedReason = SkippedRecipientReason.PREVIOUS_RECIPIENT;
            progressSummarizer.registerSucces(event.getRecipient());
        }

        String telegramId = settings.getValue(TELEGRAM_ID.getKey(), String.class);
        historyManager.saveHistory(telegramId, event.getRecipient(), skippedReason);

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
        if (settings.getValue(UPDATE_AFTER_EVERY_TELEGRAM.getKey(), Boolean.class)) {
            sendTelegramsAndUpdateRecipientsAfterEveryTelegram();
        } else {
            sendTelegrams();
        }
    }

    private void sendTelegramsAndUpdateRecipientsAfterEveryTelegram() {
        while (recipients.length > 0 && !Thread.currentThread().isInterrupted()) {
            int index = getIndexOfNextRecipientThatCanReceive(0);

            if (Thread.currentThread().isInterrupted() || index == -1) {
                break;
            }
            executeTelegramQuery(recipients[index]);

            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            updateRecipientsFromApi();
            recipients = getRecipients();
        }
    }

    private void sendTelegrams() {
        int index = 0;
        while ((index = getIndexOfNextRecipientThatCanReceive(index)) != -1) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            executeTelegramQuery(recipients[index++]);
        }
    }

    private void performNoRecipientsFoundBehavior() throws InterruptedException {
        var event = new NoRecipientsFoundEvent(this, noRecipientsFoundTimeOut);
        log.info("No recipients were found.");

        listeners.stream().forEach((tsl) -> {
            tsl.handleNoRecipientsFound(event);
        });

        if (settings.getValue(RUN_INDEFINITELY.getKey(), Boolean.class)) {
            log.info("Sleeping for {} seconds before refreshing recipients", noRecipientsFoundTimeOut / 1000);
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

    private void executeTelegramQuery(String... recipients) {
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

    private int getIndexOfNextRecipientThatCanReceive(int startingIndex) {
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
                .map(filter -> filter.toString()).collect(Collectors.toList());
    }

    private Predicate<String> getCanReceiveTelegramPredicate() {
        switch (settings.getValue(TELEGRAM_TYPE.getKey(), TelegramType.class)) {
        case RECRUITMENT:
            return recipient -> canReceiveSpecialTelegram(NationShard.CAN_RECEIVE_RECRUITMENT_TELEGRAMS, recipient,
                    nation -> !nation.isCanReceiveRecruitmentTelegrams() ? SkippedRecipientReason.BLOCKING_RECRUITMENT
                            : null);
        case CAMPAIGN:
            return recipient -> canReceiveSpecialTelegram(NationShard.CAN_RECEIVE_CAMPAIGN_TELEGRAMS, recipient,
                    nation -> !nation.isCanReceiveCampaignTelegrams() ? SkippedRecipientReason.BLOCKING_CAMPAIGN
                            : null);
        default:
            return recipient -> true; // TODO: Normal telegrams should also be checked with a simple nation name
                                      // query.
        }
    }

    private boolean canReceiveSpecialTelegram(NationShard canReceiveTelegramShard, String recipient,
            Function<Nation, SkippedRecipientReason> canReceive) {
        SkippedRecipientReason skippedReason = null;
        try {
            var nation = nationStates.getNation(recipient).shards(canReceiveTelegramShard)
                    .canReceiveTelegramFromRegion(settings.getValue(FROM_REGION.getKey(), String.class)).execute();
            skippedReason = canReceive.apply(nation);
        } catch (NationStatesResourceNotFoundException ex) {
            skippedReason = SkippedRecipientReason.NOT_FOUND;

        } catch (Exception ex) {
            log.error("An error occured while checking whether a recipient can receive the telegram", ex);
            skippedReason = SkippedRecipientReason.ERROR;
        }

        if (skippedReason != null) {
            progressSummarizer.registerFailure(recipient, skippedReason);
            historyManager.saveHistory(settings.getValue(TELEGRAM_ID.getKey(), String.class), recipient, skippedReason);
            var event = new RecipientRemovedEvent(this, recipient, skippedReason);
            log.info("Skipping recipient '{}' for the following reason: {}", recipient, skippedReason);

            listeners.stream().forEach((tsl) -> {
                tsl.handleRecipientRemoved(event);
            });
            return false;
        }
        return true;
    }
}
