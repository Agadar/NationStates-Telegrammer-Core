package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

import lombok.NonNull;

/**
 * Simply supplies whatever nation names were provided.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsProvider implements RecipientsProvider {

    public final Collection<String> nations;

    public NationsProvider(@NonNull Collection<String> nations) {
        this.nations = nations;
    }

    @Override
    public Collection<String> getRecipients() {
        return nations;
    }

    @Override
    public String toString() {
        return RecipientsFilterType.NATIONS.toString() + " " + nations.toString();
    }

}
