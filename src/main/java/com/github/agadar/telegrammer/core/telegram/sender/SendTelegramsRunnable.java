package com.github.agadar.telegrammer.core.telegram.sender;

import java.util.Set;
import java.util.function.Predicate;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.nation.Nation;
import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.nationstates.event.TelegramSentListener;
import com.github.agadar.nationstates.query.TelegramQuery;
import com.github.agadar.nationstates.shard.NationShard;
import com.github.agadar.telegrammer.core.properties.ApplicationProperties;
import com.github.agadar.telegrammer.core.recipients.listbuilder.IRecipientsListBuilder;
import com.github.agadar.telegrammer.core.telegram.QueuedStats;
import com.github.agadar.telegrammer.core.telegram.SkippedRecipientReason;
import com.github.agadar.telegrammer.core.telegram.TelegramType;
import com.github.agadar.telegrammer.core.telegram.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.telegram.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.telegram.event.RecipientsRefreshedEvent;
import com.github.agadar.telegrammer.core.telegram.event.StoppedSendingEvent;
import com.github.agadar.telegrammer.core.telegram.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.telegram.history.ITelegramHistory;

/**
 * Runnable used by TelegramManager which sends the telegrams to the recipients.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class SendTelegramsRunnable implements Runnable, TelegramSentListener {

    private final IRecipientsListBuilder recipientsListBuilder;
    private final Set<TelegramManagerListener> listeners;
    private final int noRecipientsFoundTimeOut;
    private final QueuedStats queuedStats = new QueuedStats();
    private final INationStates nationStates;
    private final ITelegramHistory historyManager;
    private final ApplicationProperties properties;

    public SendTelegramsRunnable(IRecipientsListBuilder recipientsListBuilder, INationStates nationStates,
            ITelegramHistory historyManager, ApplicationProperties properties, Set<TelegramManagerListener> listeners,
            int noRecipientsFoundTimeOut) {
        this.recipientsListBuilder = recipientsListBuilder;
        this.nationStates = nationStates;
        this.historyManager = historyManager;
        this.properties = properties;
        this.listeners = listeners;
        this.noRecipientsFoundTimeOut = noRecipientsFoundTimeOut;
    }

    @Override
    public void run() {
        try {
            do {
                // Just get the recipients, as we expect the API to already have been called
                // before running.
                String[] recipients = this.getRecipients();

                // If there are recipients available, send telegrams.
                if (recipients.length > 0) {

                    if (properties.lastTelegramType == TelegramType.NORMAL || properties.lastTelegramType == null) {
                        if (!properties.updateRecipientsAfterEveryTelegram) {
                            sendTelegram(recipients);
                        } else {
                            while (recipients.length > 0 && !Thread.currentThread().isInterrupted()) {
                                sendTelegram(recipients[0]);

                                if (Thread.currentThread().isInterrupted()) {
                                    break;
                                }
                                this.updateRecipientsFromApi();
                                recipients = this.getRecipients();
                            }
                        }
                    } else {
                        final Predicate<String> canReceivePredicate = this
                                .getCanReceiveTelegramPredicate(properties.lastTelegramType);

                        if (properties.updateRecipientsAfterEveryTelegram) {
                            while (recipients.length > 0 && !Thread.currentThread().isInterrupted()) {
                                int index = this.getIndexOfNextRecipientThatCanReceive(canReceivePredicate, recipients,
                                        0);

                                if (Thread.currentThread().isInterrupted() || index == -1) {
                                    break;
                                }
                                sendTelegram(recipients[index]);

                                if (Thread.currentThread().isInterrupted()) {
                                    break;
                                }
                                this.updateRecipientsFromApi();
                                recipients = this.getRecipients();
                            }
                        } else {
                            int index = 0;
                            while ((index = this.getIndexOfNextRecipientThatCanReceive(canReceivePredicate, recipients,
                                    index)) != -1) {

                                if (Thread.currentThread().isInterrupted()) {
                                    break;
                                }
                                sendTelegram(recipients[index++]);
                            }
                        }
                    }
                    // Else if the recipients list is empty...
                } else {
                    final NoRecipientsFoundEvent event = new NoRecipientsFoundEvent(this, noRecipientsFoundTimeOut);
                    synchronized (listeners) {
                        listeners.stream().forEach((tsl) -> {
                            tsl.handleNoRecipientsFound(event);
                        });
                    }

                    // If running indefinitely, then sleep and afterwards refresh the list.
                    if (properties.runIndefinitely) {
                        Thread.sleep(noRecipientsFoundTimeOut);
                        this.updateRecipientsFromApi();
                    }
                }
            } while (!Thread.currentThread().isInterrupted() && properties.runIndefinitely);
        } catch (InterruptedException ex) {
            /* Just fall through to finally. */
        } finally {
            final StoppedSendingEvent stoppedEvent = new StoppedSendingEvent(this, false, null,
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
        if (event.isQueued()) {
            historyManager.saveHistory(properties.telegramId, event.getRecipient(),
                    SkippedRecipientReason.PREVIOUS_RECIPIENT);
            queuedStats.registerSucces(event.getRecipient());
        } else {
            queuedStats.registerFailure(event.getRecipient(), null);
        }

        synchronized (listeners) {
            listeners.stream().forEach((tsl) -> {
                tsl.handleTelegramSent(event);
            });
        }
    }

    /**
     * Gets the recipients, applying the filters and the history but not calling the
     * API.
     * 
     * @return
     */
    private String[] getRecipients() {
        final Set<String> recipientsSet = recipientsListBuilder.getRecipients();
        return recipientsSet.toArray(new String[recipientsSet.size()]);
    }

    /**
     * Updates the recipients from the API.
     */
    private void updateRecipientsFromApi() {
        recipientsListBuilder.refreshFilters();
        final RecipientsRefreshedEvent refrevent = new RecipientsRefreshedEvent(this);
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
        final TelegramQuery q = nationStates
                .sendTelegrams(properties.clientKey, properties.telegramId, properties.secretKey, recipients)
                .addListeners(this);

        if (properties.lastTelegramType == TelegramType.RECRUITMENT) {
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
            final Nation n = nationStates.getNation(recipient).shards(NationShard.CAN_RECEIVE_RECRUITMENT_TELEGRAMS)
                    .canReceiveTelegramFromRegion(properties.fromRegion).execute();
            final SkippedRecipientReason reason = (n == null) ? SkippedRecipientReason.NOT_FOUND
                    : !n.isCanReceiveRecruitmentTelegrams() ? SkippedRecipientReason.BLOCKING_RECRUITMENT : null;
            return canReceiveXTelegrams(reason, recipient);
        } catch (Exception ex) {
            // If for any reason the call failed, just take the gamble and say
            // that the recipient can receive the telegram.
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
            final Nation n = nationStates.getNation(recipient).shards(NationShard.CAN_RECEIVE_CAMPAIGN_TELEGRAMS)
                    .canReceiveTelegramFromRegion(properties.fromRegion).execute();
            final SkippedRecipientReason reason = (n == null) ? SkippedRecipientReason.NOT_FOUND
                    : !n.isCanReceiveCampaignTelegrams() ? SkippedRecipientReason.BLOCKING_CAMPAIGN : null;
            return canReceiveXTelegrams(reason, recipient);
        } catch (Exception ex) {
            // If for any reason the call failed, just take the gamble and say
            // that the recipient can receive the telegram.
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
            historyManager.saveHistory(properties.telegramId, recipient, reason);
            final RecipientRemovedEvent event = new RecipientRemovedEvent(this, recipient, reason);

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
     * @param telegramType
     * @return
     */
    private Predicate<String> getCanReceiveTelegramPredicate(TelegramType telegramType) {
        switch (telegramType) {
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
     * @param recipientsArray
     * @param startingIndex
     * @return -1 If none was found or the thread was interrupted, otherwise an
     *         index < recipientsArray length.
     */
    private int getIndexOfNextRecipientThatCanReceive(Predicate<String> canReceiveTelegramPredicate,
            String[] recipientsArray, int startingIndex) {

        if (startingIndex >= recipientsArray.length || Thread.currentThread().isInterrupted()) {
            return -1;
        }

        while (!canReceiveTelegramPredicate.test(recipientsArray[startingIndex])) {
            startingIndex++;

            if (startingIndex >= recipientsArray.length || Thread.currentThread().isInterrupted()) {
                return -1;
            }
        }
        return startingIndex;
    }
}
