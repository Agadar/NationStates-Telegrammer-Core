package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Set;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.domain.common.happening.EjectedHappening;
import com.github.agadar.nationstates.enumerator.HappeningsFilter;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;

/**
 * Fetches recently ejected nations from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class EjectedNationsProvider extends RecipientsProvider {

    public EjectedNationsProvider(INationStates nationStates) {
        super(nationStates);
    }

    @Override
    public Set<String> getRecipients() {
        return nationStates.getWorld(WorldShard.HAPPENINGS)
                .happeningsFilter(HappeningsFilter.EJECT)
                .execute()
                .getHappenings().stream()
                .filter(happening -> happening instanceof EjectedHappening)
                .map(happening -> ((EjectedHappening) happening).getEjectedNation())
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return RecipientsProviderType.EJECTED_NATIONS.toString();
    }

}
