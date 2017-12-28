package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import java.util.HashSet;

/**
 * Simply supplies whatever nation names were provided.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationsProvider implements IRecipientsProvider {

    private final HashSet<String> nations;

    public NationsProvider(HashSet<String> nations) {
        this.nations = nations;
    }

    @Override
    public HashSet<String> getRecipients() {
        return nations;
    }

    @Override
    public String toString() {
        return RecipientsProviderType.NATIONS.toString() + " " + nations.toString();
    }

}
