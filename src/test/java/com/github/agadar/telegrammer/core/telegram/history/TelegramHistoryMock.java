package com.github.agadar.telegrammer.core.telegram.history;

import com.github.agadar.telegrammer.core.telegram.SkippedRecipientReason;

import java.util.Collection;

public class TelegramHistoryMock implements ITelegramHistory {

    @Override
    public SkippedRecipientReason getSkippedRecipientReason(String telegramId, String recipient) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean saveHistory(String telegramId, String recipient, SkippedRecipientReason reason) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean loadHistory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeOldRecipients(Collection<String> nations) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
