package com.github.agadar.telegrammer.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.github.agadar.nationstates.DefaultNationStatesImpl;
import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.exception.NationStatesAPIException;
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
import lombok.Setter;

/**
 * The default starting point for consumers of this telegrammer library for the
 * NationStates API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class DefaultTelegrammerImpl implements Telegrammer {

    private final int noAddresseesFoundTimeout = 60000;
    private final Collection<TelegrammerListener> listeners = new HashSet<>();

    @Getter
    private final NationStates nationStates;
    private final TelegramHistory telegramHistory;
    @Getter
    private final CoreSettings coreSettings;
    private final RecipientsFilterTranslator filterTranslator;

    @Getter
    @Setter
    private TelegrammerState state = TelegrammerState.IDLE;
    private Thread telegramThread;
    private SendTelegramsRunnable sendTelegramsRunnable;

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
    public RecipientsFilter createFilter(@NonNull RecipientsFilterType filterType,
            @NonNull RecipientsFilterAction filterAction,
            @NonNull Collection<String> input) {
        return filterTranslator.toFilter(filterType, filterAction, input);
    }

    @Override
    public void startSending() {

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

        // Check to make sure the thread is not already running to prevent
        // synchronization issues.
        if (telegramThread != null && telegramThread.isAlive()) {
            throw new IllegalThreadStateException("Telegram thread already running!");
        }

        // Make sure there is at least one recipient to send the telegram to.
        if (coreSettings.getFilters().getRecipients(coreSettings.getTelegramId()).isEmpty()) {
            throw new IllegalArgumentException("Please supply at least one recipient!");
        }

        // Update settings.
        coreSettings.savePropertiesFile();

        // Prepare the runnable.
        sendTelegramsRunnable = new SendTelegramsRunnable(listeners, noAddresseesFoundTimeout, nationStates,
                telegramHistory, coreSettings);
        telegramThread = new Thread(sendTelegramsRunnable);
        telegramThread.start();
    }

    @Override
    public ProgressSummary getProgressSummary() {
        return sendTelegramsRunnable.getProgressSummary();
    }

    @Override
    public void stopSending() {
        if (telegramThread != null) {
            telegramThread.interrupt();
            telegramThread = null;
            sendTelegramsRunnable = null;
        }
    }
}
