package com.github.agadar.telegrammer.core.telegramhistory;

import com.github.agadar.telegrammer.core.propertiesmanager.IPropertiesManager;
import com.github.agadar.telegrammer.core.util.SkippedRecipientReason;
import com.github.agadar.telegrammer.core.util.Tuple;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public final class TelegramHistory implements ITelegramHistory {

    /**
     * Default file name.
     */
    private final Path HISTORY_FILE = Paths.get(".nationstates-telegrammer.history");

    /**
     * Default split string.
     */
    private final String SPLITSTRING = ",";

    /**
     * The history data retrieved from and saved to the file.
     */
    private Map<Tuple<String, String>, SkippedRecipientReason> history;

    private final IPropertiesManager propertiesManager;

    public TelegramHistory(IPropertiesManager propertiesManager) {
        this.propertiesManager = propertiesManager;
    }

    @Override
    public SkippedRecipientReason getSkippedRecipientReason(String telegramId, String recipient) {
        return history.get(new Tuple(telegramId, recipient));
    }

    @Override
    public boolean saveHistory(String telegramId, String recipient, SkippedRecipientReason reason) {
        // If the history is null, instantiate it.
        if (history == null) {
            history = new HashMap<>();
        }

        // Add the new entry to the history.
        history.put(new Tuple(telegramId, recipient), reason);

        // Make sure the history file exists. If not, create it.
        if (!Files.exists(HISTORY_FILE)) {
            try {
                Files.createFile(HISTORY_FILE);
            } catch (IOException ex) {
                return false;
            }
        }

        // Persist the new entry to the history file.
        final String entry = telegramId + SPLITSTRING + recipient + SPLITSTRING
                + reason.name() + System.lineSeparator();
        try {
            Files.write(HISTORY_FILE, entry.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    @Override
    public boolean loadHistory() {
        history = new HashMap<>(); // Ensure history is new and empty.

        // Break the history file contents into lines and iterate over them.
        try (final Stream<String> lines = Files.lines(HISTORY_FILE, Charset.defaultCharset())) {
            lines.map(line -> line.split(SPLITSTRING)).filter(splitLine -> splitLine.length >= 3).forEach(splitLine -> {
                try {
                    // Try parse the reason string to the correct type. If this succeeds, the line was succesfully parsed,
                    // so we add it to the history.
                    final SkippedRecipientReason reason = SkippedRecipientReason.valueOf(splitLine[2]);
                    final Tuple<String, String> telegramIdAndRecipient = new Tuple<>(splitLine[0], splitLine[1]);
                    history.put(telegramIdAndRecipient, reason);
                } catch (IllegalArgumentException | NullPointerException ex) {
                    // Failed to parse the reason. We simply skip this line.
                }
            });
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    @Override
    public void removeOldRecipients(Collection<String> nations) {
        for (final Iterator<String> it = nations.iterator(); it.hasNext();) {
            if (getSkippedRecipientReason(propertiesManager.getTelegramId(), it.next()) != null) {
                it.remove();   // Remove recipient
            }
        }
    }
}
