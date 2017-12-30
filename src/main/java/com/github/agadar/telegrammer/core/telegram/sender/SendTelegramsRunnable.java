package com.github.agadar.telegrammer.core.telegram.sender;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.nation.Nation;
import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.nationstates.event.TelegramSentListener;
import com.github.agadar.nationstates.query.TelegramQuery;
import com.github.agadar.nationstates.shard.NationShard;
import com.github.agadar.telegrammer.core.properties.ApplicationProperties;

import com.github.agadar.telegrammer.core.telegram.SkippedRecipientReason;
import com.github.agadar.telegrammer.core.telegram.TelegramType;
import com.github.agadar.telegrammer.core.telegram.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.telegram.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.telegram.event.RecipientsRefreshedEvent;
import com.github.agadar.telegrammer.core.telegram.event.StoppedSendingEvent;
import com.github.agadar.telegrammer.core.telegram.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.telegram.QueuedStats;
import com.github.agadar.telegrammer.core.telegram.history.ITelegramHistory;
import com.github.agadar.telegrammer.core.recipients.listbuilder.IRecipientsListBuilder;

import java.util.Set;

/**
 * Runnable used by TelegramManager which sends the telegrams to the recipients.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class SendTelegramsRunnable implements Runnable, TelegramSentListener {

    private final IRecipientsListBuilder recipientsListBuilder;
    private final Set<TelegramManagerListener> listeners;
    private final int noRecipientsFoundTimeOut;
    private final QueuedStats queuedStats;

    private final INationStates nationStates;
    private final ITelegramHistory historyManager;
    private final ApplicationProperties properties;

    public SendTelegramsRunnable(IRecipientsListBuilder recipientsListBuilder, INationStates nationStates,
            ITelegramHistory historyManager, ApplicationProperties properties,
            Set<TelegramManagerListener> listeners, int noRecipientsFoundTimeOut) {
        this.recipientsListBuilder = recipientsListBuilder;
        this.nationStates = nationStates;
        this.historyManager = historyManager;
        this.properties = properties;
        this.listeners = listeners;
        this.noRecipientsFoundTimeOut = noRecipientsFoundTimeOut;
        this.queuedStats = new QueuedStats();
    }

    @Override
    public void run() {
        boolean causedByError = false;
        String errorMsg = null;

        try {
            // Loop until either the thread has been interrupted, or all filters are done.
            while (!Thread.currentThread().isInterrupted()) {
                final Set<String> recipients = recipientsListBuilder.getRecipients();

                // If there are recipients available...
                if (recipients.size() > 0) {
                    final String[] RecipArray = recipients.toArray(new String[recipients.size()]);

                    if (properties.lastTelegramType != null) {
                        // According to the telegram type, take proper action...
                        switch (properties.lastTelegramType) {
                            case RECRUITMENT: {
                                boolean skipNext = !canReceiveRecruitmentTelegrams(RecipArray[0]);
                                for (int i = 0; i < RecipArray.length && !Thread.currentThread().isInterrupted(); i++) {
                                    final boolean skipThis = skipNext;

                                    if (i < RecipArray.length - 1) {
                                        final String nextRecipient = RecipArray[i + 1];
                                        skipNext = !canReceiveRecruitmentTelegrams(nextRecipient);
                                    }

                                    if (skipThis) {
                                        continue;
                                    }
                                    sendTelegram(RecipArray[i]);
                                }
                                break;
                            }
                            case CAMPAIGN: {
                                boolean skipNext = !canReceiveCampaignTelegrams(RecipArray[0]);
                                for (int i = 0; i < RecipArray.length && !Thread.currentThread().isInterrupted(); i++) {
                                    final boolean skipThis = skipNext;

                                    if (i < RecipArray.length - 1) {
                                        final String nextRecipient = RecipArray[i + 1];
                                        skipNext = !canReceiveCampaignTelegrams(nextRecipient);
                                    }

                                    if (skipThis) {
                                        continue;
                                    }
                                    sendTelegram(RecipArray[i]);
                                }
                                break;
                            }
                            // If we're sending a normal telegram, just send it.
                            default:
                                sendTelegram(RecipArray);
                                break;
                        }
                    }
                } // Else if the recipients list is empty, sleep for a bit, then continue.
                else {
                    final NoRecipientsFoundEvent event = new NoRecipientsFoundEvent(this, noRecipientsFoundTimeOut);

                    synchronized (listeners) {
                        // Publish no recipients found event.
                        listeners.stream().forEach((tsl)
                                -> {
                            tsl.handleNoRecipientsFound(event);
                        });
                    }
                    Thread.sleep(noRecipientsFoundTimeOut);
                }

                // If not continuing indefinitely, just end it all.
                if (!properties.runIndefinitely || Thread.currentThread().isInterrupted()) {
                    break;
                }

                // Refresh the filters before going back to the top.
                recipientsListBuilder.refreshFilters();
                final RecipientsRefreshedEvent refrevent = new RecipientsRefreshedEvent(this);

                synchronized (listeners) {
                    // Publish recipients refreshed event.
                    listeners.stream().forEach((tsl)
                            -> {
                        tsl.handleRecipientsRefreshed(refrevent);
                    });
                }
            }
        } catch (InterruptedException ex) {
            /* Just fall through to finally. */
        } finally {

            // Send stopped event.
            final StoppedSendingEvent stoppedEvent = new StoppedSendingEvent(this,
                    causedByError, errorMsg, queuedStats.getQueuedSucces(),
                    queuedStats.getRecipientDidntExist(), queuedStats.getRecipientIsBlocking(),
                    queuedStats.getDisconnectOrOtherReason());
            listeners.stream().forEach((tsl)
                    -> {
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
        if (event.queued) {
            historyManager.saveHistory(properties.telegramId, event.recipient, SkippedRecipientReason.PREVIOUS_RECIPIENT);
            queuedStats.registerSucces(event.recipient);
        } else {
            queuedStats.registerFailure(event.recipient, null);
        }
        //System.out.println("--------called----1-----");
        synchronized (listeners) {
            // Pass telegram sent event through.
            listeners.stream().forEach((tsl)
                    -> {
                tsl.handleTelegramSent(event);
            });
        }
    }

    /**
     * Sends the telegram to the specified recipient(s).
     *
     * @param recipients
     */
    private void sendTelegram(String... recipients) {
        // Prepare query.
        final TelegramQuery q = nationStates.sendTelegrams(properties.clientKey, properties.telegramId, properties.secretKey,
                recipients).addListeners(this);

        // Tag as recruitment telegram if needed.
        if (properties.lastTelegramType == TelegramType.RECRUITMENT) {
            q.isRecruitment();
        }
        q.execute(null);    // send the telegrams
    }

    /**
     * Returns whether or not the recipient may receive a recruitment telegram.
     * If not, removes it from Recipients and throws a RecipientRemovedEvent. If
     * the server couldn't be reached, always returns true.
     *
     * @param recipient
     * @return
     */
    private boolean canReceiveRecruitmentTelegrams(String recipient) {
        try {
            // Make server call.
            Nation n = nationStates.getNation(recipient).shards(NationShard.CAN_RECEIVE_RECRUITMENT_TELEGRAMS)
                    .canReceiveTelegramFromRegion(properties.fromRegion).execute();
            final SkippedRecipientReason reason = (n == null) ? SkippedRecipientReason.NOT_FOUND
                    : !n.canReceiveRecruitmentTelegrams ? SkippedRecipientReason.BLOCKING_RECRUITMENT : null;
            return canReceiveXTelegrams(reason, recipient);
        } catch (Exception ex) {
            // If for any reason the call failed, just take the gamble and say 
            // that the recipient can receive the telegram.
            return true;
        }
    }

    /**
     * Returns whether or not the recipient may receive a campaign telegram. If
     * not, removes it from Recipients and throws a RecipientRemovedEvent. If
     * the server couldn't be reached, always returns true.
     *
     * @param recipient
     * @return
     */
    private boolean canReceiveCampaignTelegrams(String recipient) {
        try {
            // Make server call.
            Nation n = nationStates.getNation(recipient).shards(NationShard.CAN_RECEIVE_CAMPAIGN_TELEGRAMS).execute();
            final SkippedRecipientReason reason = (n == null) ? SkippedRecipientReason.NOT_FOUND
                    : !n.canReceiveCampaignTelegrams ? SkippedRecipientReason.BLOCKING_CAMPAIGN : null;
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
                // Pass telegram sent event through.
                listeners.stream().forEach((tsl)
                        -> {
                    tsl.handleRecipientRemoved(event);
                });
            }
            return false;
        }
        return true;
    }
}
