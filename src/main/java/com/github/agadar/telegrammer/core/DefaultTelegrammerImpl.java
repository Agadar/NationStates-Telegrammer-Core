package com.github.agadar.telegrammer.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.github.agadar.nationstates.DefaultNationStatesImpl;
import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.telegrammer.core.event.FilterRemovedEvent;
import com.github.agadar.telegrammer.core.event.FinishedRefreshingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.StartedRefreshingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.StartedSendingEvent;
import com.github.agadar.telegrammer.core.history.TelegramHistory;
import com.github.agadar.telegrammer.core.history.TelegramHistoryImpl;
import com.github.agadar.telegrammer.core.misc.TelegrammerState;
import com.github.agadar.telegrammer.core.progress.ProgressSummary;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsFilterTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsFilterTranslatorImpl;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsListBuilderTranslatorImpl;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsProviderTranslatorImpl;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccessImpl;
import com.github.agadar.telegrammer.core.runnable.SendTelegramsRunnable;
import com.github.agadar.telegrammer.core.settings.Settings;
import com.github.agadar.telegrammer.core.settings.CoreSettings;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * The default starting point for consumers of this telegrammer library for the
 * NationStates API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Slf4j
public class DefaultTelegrammerImpl implements Telegrammer {

    private final int noAddresseesFoundTimeout = 60000;
    private final Collection<TelegrammerListener> listeners = new HashSet<>();
    private final ExecutorService recipientsRefreshingExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService senderExecutor = Executors.newSingleThreadExecutor();

    @Getter
    private final NationStates nationStates;
    private final TelegramHistory telegramHistory;
    @Getter
    private final CoreSettings coreSettings;
    private final RecipientsFilterTranslator filterTranslator;

    @Getter
    private volatile TelegrammerState state = TelegrammerState.IDLE;
    private volatile Future<Void> senderFuture;
    private volatile SendTelegramsRunnable sendTelegramsRunnable;

    /**
     * Constructor.
     * 
     * @param userAgent The user agent to use for API calls. NationStates moderators
     *                  should be able to identify you and your script via your user
     *                  agent. As such, try providing at least your nation name, and
     *                  preferably include your e-mail address, a link to a website
     *                  you own, or something else that can help them contact you if
     *                  needed.
     * @param settings  The application-wide settings, to which this component adds
     *                  its own settings.
     * @throws NationStatesAPIException If initializing the underlying
     *                                  {@link NationStates} failed.
     */
    public DefaultTelegrammerImpl(@NonNull String userAgent, @NonNull Settings settings)
            throws NationStatesAPIException {
        nationStates = new DefaultNationStatesImpl(userAgent);
        var regionDumpAccess = new RegionDumpAccessImpl(nationStates);
        var providerTranslator = new RecipientsProviderTranslatorImpl(nationStates, regionDumpAccess);
        filterTranslator = new RecipientsFilterTranslatorImpl(providerTranslator);
        telegramHistory = new TelegramHistoryImpl(".nationstates-telegrammer.history");
        var listBuilderTranslator = new RecipientsListBuilderTranslatorImpl(telegramHistory, filterTranslator);
        coreSettings = new CoreSettings(settings, listBuilderTranslator);
        telegramHistory.loadHistory();
    }

    @Override
    public void addListeners(TelegrammerListener... listeners) {
        synchronized (this.listeners) {
            this.listeners.addAll(Arrays.asList(listeners));
        }
    }

    @Override
    public void removeListener(@NonNull TelegrammerListener... listeners) {
        synchronized (this.listeners) {
            this.listeners.removeAll(Arrays.asList(listeners));
        }
    }

    @Override
    public synchronized void addFilter(@NonNull RecipientsFilterType filterType,
            @NonNull RecipientsFilterAction filterAction,
            @NonNull Collection<String> input) {

        if (state != TelegrammerState.IDLE) {
            log.warn("Can't add filters while telegrammer state is '{}'", state.name());
            return;
        }
        state = TelegrammerState.REFRESHING_RECIPIENTS;
        recipientsRefreshingExecutor.execute(() -> {
            var startEvent = new StartedRefreshingRecipientsEvent(this, TelegrammerState.REFRESHING_RECIPIENTS);
            synchronized (listeners) {
                listeners.forEach(listener -> listener.handleStartedRefreshingRecipients(startEvent));
            }

            var filter = filterTranslator.toFilter(filterType, filterAction, input);
            var failedFilters = new HashMap<RecipientsFilter, NationStatesAPIException>();
            coreSettings.getFilters().addFilter(filter);
            try {
                filter.refreshFilter();
            } catch (NationStatesAPIException ex) {
                log.error("An error occured while refreshing a filter", ex);
                failedFilters.put(filter, ex);
            }
            synchronized (this) {
                state = TelegrammerState.IDLE;
            }
            var finishedEvent = new FinishedRefreshingRecipientsEvent(this, TelegrammerState.REFRESHING_RECIPIENTS,
                    failedFilters);
            synchronized (listeners) {
                listeners.forEach(listener -> listener.handleFinishedRefreshingRecipients(finishedEvent));
            }
        });
    }

    @Override
    public synchronized void removeFilterAtIndex(int index) {
        if (state != TelegrammerState.IDLE) {
            log.warn("Can't remove filters while telegrammer state is '{}'", state.name());
            return;
        }
        int numberOfFilters = coreSettings.getFilters().getFilters().size();

        if (index >= 0 && index < numberOfFilters) {
            coreSettings.getFilters().removeFilterAt(index);

        } else {
            log.error("Can't remove filter at index {}: index is < 0 or >= {}", index, numberOfFilters);
        }
        var event = new FilterRemovedEvent(this, index);
        synchronized (listeners) {
            listeners.forEach(listener -> listener.handleFilterRemoved(event));
        }
    }

    @Override
    public synchronized void refreshFilters() {
        if (state != TelegrammerState.IDLE) {
            log.warn("Can't refresh filters while telegrammer state is '{}'", state.name());
            return;
        }
        state = TelegrammerState.REFRESHING_RECIPIENTS;
        recipientsRefreshingExecutor.execute(() -> {
            var startEvent = new StartedRefreshingRecipientsEvent(this, TelegrammerState.REFRESHING_RECIPIENTS);
            synchronized (listeners) {
                listeners.forEach(listener -> listener.handleStartedRefreshingRecipients(startEvent));
            }
            var failedFilters = coreSettings.getFilters().refreshFilters();
            synchronized (this) {
                state = TelegrammerState.IDLE;
            }
            var finishedEvent = new FinishedRefreshingRecipientsEvent(this, TelegrammerState.REFRESHING_RECIPIENTS,
                    failedFilters);
            synchronized (listeners) {
                listeners.forEach(listener -> listener.handleFinishedRefreshingRecipients(finishedEvent));
            }
        });

    }

    @Override
    public synchronized void startSending() {
        if (state != TelegrammerState.IDLE) {
            log.warn("Can't start sending while telegrammer state is '{}'", state.name());
            return;
        }
        coreSettings.setFromRegion(coreSettings.getFromRegion().trim());
        coreSettings.setClientKey(removeWhiteSpaces(coreSettings.getClientKey()));
        coreSettings.setTelegramId(removeWhiteSpaces(coreSettings.getTelegramId()));
        coreSettings.setSecretKey(removeWhiteSpaces(coreSettings.getSecretKey()));
        // TODO: Settings updated event.

        // Make sure all inputs are valid.
        if (coreSettings.getClientKey().isEmpty()) {
            throw new IllegalArgumentException("Please supply a Client Key!");
        }
        if (coreSettings.getTelegramId().isEmpty()) {
            throw new IllegalArgumentException("Please supply a Telegram Id!");
        }
        if (coreSettings.getSecretKey().isEmpty()) {
            throw new IllegalArgumentException("Please supply a Secret Key!");
        }

        // Make sure there is at least one recipient to send the telegram to.
        if (coreSettings.getFilters().getRecipients(coreSettings.getTelegramId()).isEmpty()) {
            throw new IllegalArgumentException("Please supply at least one recipient!");
        }
        state = TelegrammerState.QUEUING_TELEGRAMS;

        // Update settings.
        coreSettings.savePropertiesFile();

        var event = new StartedSendingEvent(this);
        synchronized (listeners) {
            listeners.forEach(listener -> listener.handleStartedSending(event));
        }

        // Prepare the runnable.
        sendTelegramsRunnable = new SendTelegramsRunnable(listeners, noAddresseesFoundTimeout, nationStates,
                telegramHistory, coreSettings);
        senderFuture = senderExecutor.submit(sendTelegramsRunnable, null);
    }

    @Override
    public ProgressSummary getProgressSummary() {
        if (sendTelegramsRunnable != null) {
            return sendTelegramsRunnable.getProgressSummary();
        }
        return ProgressSummary.builder()
                .disconnectOrOtherReason(0)
                .queuedSucces(0)
                .recipientDidntExist(0)
                .recipientIsBlocking(0).build();
    }

    @Override
    public synchronized void stopSending() {
        if (state != TelegrammerState.QUEUING_TELEGRAMS) {
            log.warn("Can't stop sending while telegrammer state is '{}'", state.name());
            return;
        }
        if (senderFuture != null) {
            senderFuture.cancel(true);
            senderFuture = null;
            sendTelegramsRunnable = null;
        }
        state = TelegrammerState.IDLE;
    }

    private String removeWhiteSpaces(String target) {
        return target.replace(" ", "");
    }
}
