package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;

import com.github.agadar.nationstates.NationStates;
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
public class RefoundedNationsProvider extends NationStatesRecipientsProvider {

    public RefoundedNationsProvider(@NonNull NationStates nationStates) {
        super(nationStates);
    }

    @Override
    public Collection<String> getRecipients() {
        var happenings = nationStates.getWorld(WorldShard.HAPPENINGS).happeningsFilter(HappeningsFilter.FOUNDING)
                .execute().getHappenings();
        return StringFunctions.extractNationsFromHappenings(happenings, StringFunctions.KeyWord.refounded);
    }

    @Override
    public String toString() {
        return RecipientsProviderType.REFOUNDED_NATIONS.toString();
    }

}
