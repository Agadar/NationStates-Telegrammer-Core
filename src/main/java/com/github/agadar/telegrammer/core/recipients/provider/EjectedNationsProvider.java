package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.common.happening.EjectedHappening;
import com.github.agadar.nationstates.enumerator.HappeningsFilter;
import com.github.agadar.nationstates.exception.NationStatesAPIException;
import com.github.agadar.nationstates.shard.WorldShard;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;

import lombok.NonNull;

/**
 * Fetches recently ejected nations from the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class EjectedNationsProvider extends NationStatesRecipientsProvider {

    public EjectedNationsProvider(@NonNull NationStates nationStates) {
        super(nationStates, RecipientsFilterType.EJECTED_NATIONS);
    }

    @Override
    public Collection<String> getRecipients() throws NationStatesAPIException {
        return nationStates.getWorld(WorldShard.HAPPENINGS)
                .happeningsFilter(HappeningsFilter.EJECT)
                .execute()
                .getHappenings().stream()
                .filter(happening -> happening instanceof EjectedHappening)
                .map(happening -> ((EjectedHappening) happening).getEjectedNation())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
