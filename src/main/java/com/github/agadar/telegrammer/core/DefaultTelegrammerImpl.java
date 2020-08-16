package com.github.agadar.telegrammer.core;

import static com.github.agadar.telegrammer.core.settings.CoreSettingKey.CLIENT_KEY;
import static com.github.agadar.telegrammer.core.settings.CoreSettingKey.FILTERS;
import static com.github.agadar.telegrammer.core.settings.CoreSettingKey.FROM_REGION;
import static com.github.agadar.telegrammer.core.settings.CoreSettingKey.RUN_INDEFINITELY;
import static com.github.agadar.telegrammer.core.settings.CoreSettingKey.SECRET_KEY;
import static com.github.agadar.telegrammer.core.settings.CoreSettingKey.TELEGRAM_ID;
import static com.github.agadar.telegrammer.core.settings.CoreSettingKey.TELEGRAM_TYPE;
import static com.github.agadar.telegrammer.core.settings.CoreSettingKey.UPDATE_AFTER_EVERY_TELEGRAM;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.DefaultNationStatesImpl;
import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.telegrammer.core.event.FilterRemovedEvent;
import com.github.agadar.telegrammer.core.event.FinishedRefreshingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.SettingsUpdatedEvent;
import com.github.agadar.telegrammer.core.event.StartedRefreshingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.StartedSendingEvent;
import com.github.agadar.telegrammer.core.history.TelegramHistory;
import com.github.agadar.telegrammer.core.history.TelegramHistoryImpl;
import com.github.agadar.telegrammer.core.misc.TelegramType;
import com.github.agadar.telegrammer.core.misc.TelegrammerState;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.recipients.listbuilder.RecipientsListBuilder;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsFilterTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsFilterTranslatorImpl;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsListBuilderTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsListBuilderTranslatorImpl;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsProviderTranslatorImpl;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccessImpl;
import com.github.agadar.telegrammer.core.runnable.SendTelegramsRunnable;
import com.github.agadar.telegrammer.core.settings.CoreSettingKey;
import com.github.agadar.telegrammer.core.settings.Setting;
import com.github.agadar.telegrammer.core.settings.Settings;

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

    private final NationStates nationStates;
    private final TelegramHistory telegramHistory;
    private final Settings settings;
    private final RecipientsFilterTranslator filterTranslator;

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
        this.settings = settings;

        settings.addStringSetting(CLIENT_KEY.getKey(), "");
        settings.addStringSetting(FROM_REGION.getKey(), "");
        settings.addStringSetting(SECRET_KEY.getKey(), "");
        settings.addStringSetting(TELEGRAM_ID.getKey(), "");
        settings.addBooleanSetting(RUN_INDEFINITELY.getKey(), false);
        settings.addBooleanSetting(UPDATE_AFTER_EVERY_TELEGRAM.getKey(), false);
        settings.addSetting(createTelegramTypeSetting());
        settings.addSetting(createFiltersSetting(listBuilderTranslator));

        telegramHistory.loadHistory();
    }

    @Override
    public void addListeners(TelegrammerListener... listeners) {
        synchronized (this.listeners) {
            this.listeners.addAll(Arrays.asList(listeners));
            var event = createSettingsUpdatedEvent();
            for (var listener : listeners) {
                listener.handleSettingsUpdated(event);
            }
        }
    }

    @Override
    public void removeListener(@NonNull TelegrammerListener... listeners) {
        synchronized (this.listeners) {
            this.listeners.removeAll(Arrays.asList(listeners));
        }
    }

    @Override
    public synchronized void updateSetting(@NonNull CoreSettingKey key, @NonNull Object value) {
        if (state != TelegrammerState.IDLE) {
            log.warn("Can't update settings while telegrammer state is '{}'", state.name());
            return;
        }
        if (settings.setValue(key.getKey(), value)) {
            publishSettingsUpdatedEvent();
        }
    }

    @Override
    public synchronized void addFilter(@NonNull RecipientsFilterType filterType,
            @NonNull RecipientsFilterAction filterAction, @NonNull Collection<String> input) {

        if (state != TelegrammerState.IDLE) {
            log.warn("Can't add filters while telegrammer state is '{}'", state.name());
            return;
        }
        state = TelegrammerState.REFRESHING_RECIPIENTS;
        recipientsRefreshingExecutor.execute(() -> {
            publishStartedRefreshingRecipientsEvent();
            var filter = filterTranslator.toFilter(filterType, filterAction, input);
            var failedFilters = new HashMap<RecipientsFilter, NationStatesAPIException>();
            settings.getValue(FILTERS.getKey(), RecipientsListBuilder.class).addFilter(filter);
            try {
                filter.refreshFilter();
            } catch (NationStatesAPIException ex) {
                log.error("An error occured while refreshing a filter", ex);
                failedFilters.put(filter, ex);
            }
            publishFinishedRefreshingRecipientsEvent(failedFilters);
            synchronized (this) {
                state = TelegrammerState.IDLE;
            }
        });
    }

    @Override
    public synchronized void removeFilterAtIndex(int index) {
        if (state != TelegrammerState.IDLE) {
            log.warn("Can't remove filters while telegrammer state is '{}'", state.name());
            return;
        }
        var filters = settings.getValue(FILTERS.getKey(), RecipientsListBuilder.class);
        int numberOfFilters = filters.getFilters().size();

        if (index >= 0 && index < numberOfFilters) {
            filters.removeFilterAt(index);

        } else {
            log.error("Can't remove filter at index {}: index is < 0 or >= {}", index, numberOfFilters);
        }
        synchronized (listeners) {
            var event = new FilterRemovedEvent(this, stringifyFilters(), getNumberOfRecipients());
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
            publishStartedRefreshingRecipientsEvent();
            var filters = settings.getValue(FILTERS.getKey(), RecipientsListBuilder.class);
            var failedFilters = filters.refreshFilters();
            synchronized (this) {
                state = TelegrammerState.IDLE;
            }
            publishFinishedRefreshingRecipientsEvent(failedFilters);
        });
    }

    @Override
    public synchronized void startSending() {
        if (state != TelegrammerState.IDLE) {
            log.warn("Can't start sending while telegrammer state is '{}'", state.name());
            return;
        }
        settings.setValue(FROM_REGION.getKey(), settings.getValue(FROM_REGION.getKey(), String.class).trim());
        settings.setValue(CLIENT_KEY.getKey(), removeWhiteSpaces(settings.getValue(CLIENT_KEY.getKey(), String.class)));
        settings.setValue(TELEGRAM_ID.getKey(),
                removeWhiteSpaces(settings.getValue(TELEGRAM_ID.getKey(), String.class)));
        settings.setValue(SECRET_KEY.getKey(), removeWhiteSpaces(settings.getValue(SECRET_KEY.getKey(), String.class)));
        publishSettingsUpdatedEvent();

        // Make sure all inputs are valid.
        String telegramId = settings.getValue(TELEGRAM_ID.getKey(), String.class);
        if (settings.getValue(CLIENT_KEY.getKey(), String.class).isEmpty()) {
            throw new IllegalArgumentException("Please supply a Client Key!");
        }
        if (telegramId.isEmpty()) {
            throw new IllegalArgumentException("Please supply a Telegram Id!");
        }
        if (settings.getValue(SECRET_KEY.getKey(), String.class).isEmpty()) {
            throw new IllegalArgumentException("Please supply a Secret Key!");
        }

        // Make sure there is at least one recipient to send the telegram to.
        var filters = settings.getValue(FILTERS.getKey(), RecipientsListBuilder.class);
        if (filters.getRecipients(telegramId).isEmpty()) {
            throw new IllegalArgumentException("Please supply at least one recipient!");
        }
        state = TelegrammerState.QUEUING_TELEGRAMS;

        // Update settings.
        settings.savePropertiesFile();

        var event = new StartedSendingEvent(this);
        synchronized (listeners) {
            listeners.forEach(listener -> listener.handleStartedSending(event));
        }

        // Prepare the runnable.
        sendTelegramsRunnable = new SendTelegramsRunnable(listeners, noAddresseesFoundTimeout, nationStates, telegramHistory, settings);
        senderFuture = senderExecutor.submit(sendTelegramsRunnable, null);
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

    private void publishSettingsUpdatedEvent() {
        synchronized (listeners) {
            var event = createSettingsUpdatedEvent();
            listeners.forEach(listener -> listener.handleSettingsUpdated(event));
        }
    }

    private SettingsUpdatedEvent createSettingsUpdatedEvent() {
        var event = SettingsUpdatedEvent.builder()
                .clientKey(settings.getValue(CLIENT_KEY.getKey(), String.class))
                .filters(stringifyFilters())
                .fromRegion(settings.getValue(FROM_REGION.getKey(), String.class))
                .runIndefinitely(settings.getValue(RUN_INDEFINITELY.getKey(), Boolean.class))
                .secretKey(settings.getValue(SECRET_KEY.getKey(), String.class))
                .source(this)
                .telegramId(settings.getValue(TELEGRAM_ID.getKey(), String.class))
                .telegramType(settings.getValue(TELEGRAM_TYPE.getKey(), TelegramType.class))
                .updateAfterEveryTelegram(settings.getValue(UPDATE_AFTER_EVERY_TELEGRAM.getKey(), Boolean.class))
                .numberOfRecipients(getNumberOfRecipients())
                .build();
        return event;
    }

    private void publishStartedRefreshingRecipientsEvent() {
        synchronized (listeners) {
            var startEvent = new StartedRefreshingRecipientsEvent(this, state);
            listeners.forEach(listener -> listener.handleStartedRefreshingRecipients(startEvent));
        }
    }

    private void publishFinishedRefreshingRecipientsEvent(
            Map<RecipientsFilter, NationStatesAPIException> failedFilters) {
        synchronized (listeners) {
            int numberOfRecipients = getNumberOfRecipients();
            var finishedEvent = FinishedRefreshingRecipientsEvent.builder()
                    .failedFilters(failedFilters)
                    .filters(stringifyFilters())
                    .numberOfRecipients(numberOfRecipients)
                    .source(this)
                    .telegrammerState(TelegrammerState.REFRESHING_RECIPIENTS).build();
            listeners.forEach(listener -> listener.handleFinishedRefreshingRecipients(finishedEvent));
        }
    }

    private int getNumberOfRecipients() {
        String telegramId = settings.getValue(TELEGRAM_ID.getKey(), String.class);
        var filters = settings.getValue(FILTERS.getKey(), RecipientsListBuilder.class);
        return filters.getRecipients(telegramId).size();
    }

    private List<String> stringifyFilters() {
        return settings.getValue(FILTERS.getKey(), RecipientsListBuilder.class).getFilters().stream()
                .map(filter -> filter.toString())
                .collect(Collectors.toList());
    }

    private Setting<TelegramType> createTelegramTypeSetting() {
        return new Setting<TelegramType>(TELEGRAM_TYPE.getKey(), TelegramType.class,
                text -> Enum.valueOf(TelegramType.class, text), telegramType -> telegramType.name(),
                TelegramType.NORMAL);
    }

    private Setting<RecipientsListBuilder> createFiltersSetting(RecipientsListBuilderTranslator builderTranslator) {
        return new Setting<RecipientsListBuilder>(FILTERS.getKey(), RecipientsListBuilder.class,
                text -> builderTranslator.toBuilder(text), builder -> builder.toConfigurationString(),
                builderTranslator.toBuilder(""));
    }

    private String removeWhiteSpaces(String target) {
        return target.replace(" ", "");
    }
}
