package com.github.agadar.telegrammer.core.telegram.history;

import com.github.agadar.telegrammer.core.properties.ApplicationProperties;
import com.github.agadar.telegrammer.core.telegram.SkippedRecipientReason;
import com.github.agadar.telegrammer.core.util.Tuple;

import lombok.NonNull;

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

public class TelegramHistory implements ITelegramHistory {

    /**
     * Default split string.
     */
    private final String SPLITSTRING = ",";

    /**
     * The history data retrieved from and saved to the file.
     */
    private Map<Tuple<String, String>, SkippedRecipientReason> history;

    /**
     * Path to the history file.
     */
    private final Path historyFile;

    private final ApplicationProperties properties;

    public TelegramHistory(@NonNull ApplicationProperties properties, @NonNull String historyFileName) {
        this.properties = properties;
        this.historyFile = Paths.get(historyFileName);
    }

    @Override
    public SkippedRecipientReason getSkippedRecipientReason(@NonNull String telegramId, @NonNull String recipient) {
        return history.get(new Tuple<String, String>(telegramId, recipient));
    }

    @Override
    public boolean saveHistory(@NonNull String telegramId, @NonNull String recipient,
            @NonNull SkippedRecipientReason reason) {
        // If the history is null, instantiate it.
        if (history == null) {
            history = new HashMap<>();
        }

        // Add the new entry to the history.
        history.put(new Tuple<String, String>(telegramId, recipient), reason);

        // Make sure the history file exists. If not, create it.
        if (!Files.exists(this.historyFile)) {
            try {
                Files.createFile(this.historyFile);
            } catch (IOException ex) {
                return false;
            }
        }

        // Persist the new entry to the history file.
        final String entry = telegramId + SPLITSTRING + recipient + SPLITSTRING + reason.name()
                + System.lineSeparator();
        try {
            Files.write(this.historyFile, entry.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    @Override
    public boolean loadHistory() {
        history = new HashMap<>(); // Ensure history is new and empty.

        // Break the history file contents into lines and iterate over them.
        try (final Stream<String> lines = Files.lines(this.historyFile, Charset.defaultCharset())) {
            lines.map(line -> line.split(SPLITSTRING)).filter(splitLine -> splitLine.length >= 3).forEach(splitLine -> {
                try {
                    // Try parse the reason string to the correct type. If this succeeds, the line
                    // was succesfully parsed,
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
    public void removeOldRecipients(@NonNull Collection<String> nations) {
        for (final Iterator<String> it = nations.iterator(); it.hasNext();) {
            if (getSkippedRecipientReason(properties.getTelegramId(), it.next()) != null) {
                it.remove(); // Remove recipient
            }
        }
    }
}
