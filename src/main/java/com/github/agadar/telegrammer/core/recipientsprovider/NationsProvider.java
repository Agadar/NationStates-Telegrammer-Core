package com.github.agadar.telegrammer.core.recipientsprovider;

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

}
