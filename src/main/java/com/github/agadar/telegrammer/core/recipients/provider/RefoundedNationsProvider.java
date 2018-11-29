package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Set;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.enumerator.HappeningsFilter;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.util.StringFunctions;

import lombok.NonNull;

/**
 * Fetches recently refounded nations from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RefoundedNationsProvider extends RecipientsProvider {

    public RefoundedNationsProvider(@NonNull INationStates nationStates) {
        super(nationStates);
    }

    @Override
    public Set<String> getRecipients() {
        var happenings = nationStates.getWorld(WorldShard.HAPPENINGS).happeningsFilter(HappeningsFilter.FOUNDING)
                .execute().getHappenings();
        return StringFunctions.extractNationsFromHappenings(happenings, StringFunctions.KeyWord.refounded);
    }

    @Override
    public String toString() {
        return RecipientsProviderType.REFOUNDED_NATIONS.toString();
    }

}
