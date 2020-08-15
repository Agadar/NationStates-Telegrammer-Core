package com.github.agadar.telegrammer.core.history;

import java.util.Collection;

import com.github.agadar.telegrammer.core.misc.SkippedRecipientReason;

public class TelegramHistoryMock implements TelegramHistory {

    @Override
    public SkippedRecipientReason getSkippedRecipientReason(String telegramId, String recipient) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean saveHistory(String telegramId, String recipient, SkippedRecipientReason reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean loadHistory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeOldRecipients(Collection<String> nations, String telegramId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
