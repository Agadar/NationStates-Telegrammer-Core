package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

import lombok.NonNull;

import java.util.Set;

/**
 * Simply supplies whatever nation names were provided.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsProvider implements IRecipientsProvider {

    public final Set<String> nations;

    public NationsProvider(@NonNull Set<String> nations) {
        this.nations = nations;
    }

    @Override
    public Set<String> getRecipients() {
        return nations;
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NATIONS.toString() + " " + nations.toString();
    }

}
