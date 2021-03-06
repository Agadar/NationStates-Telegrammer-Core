package com.github.agadar.telegrammer.core.recipients.translator;

import java.util.Collection;
import java.util.HashSet;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.enumerator.RegionTag;
import com.github.agadar.telegrammer.core.misc.StringFunctions;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.recipients.provider.AllNationsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.DelegatesProvider;
import com.github.agadar.telegrammer.core.recipients.provider.EjectedNationsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NationsInEmbassyRegionsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NationsInRegionsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NationsInRegionsWithTagsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NationsInRegionsWithoutTagsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NationsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NewDelegatesProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NewNationsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NewWorldAssemblyMembersProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NullRecipientsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.RecipientsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.RefoundedNationsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.WorldAssemblyMembersProvider;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccess;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecipientsProviderTranslatorImpl implements RecipientsProviderTranslator {

    private final NationStates nationStates;
    private final RegionDumpAccess regionDumpAccess;

    public RecipientsProviderTranslatorImpl(NationStates nationStates, RegionDumpAccess regionDumpAccess) {
        this.nationStates = nationStates;
        this.regionDumpAccess = regionDumpAccess;
    }

    @Override
    public RecipientsProvider toProvider(RecipientsFilterType filterType, Collection<String> input) {
        if (filterType == null) {
            return new NullRecipientsProvider();
        }
        input = normalizeNames(input);
        switch (filterType) {

            case ALL_NATIONS:
                return new AllNationsProvider(nationStates);

            case EJECTED_NATIONS:
                return new EjectedNationsProvider(nationStates);

            case NATIONS_IN_EMBASSY_REGIONS:
                return new NationsInEmbassyRegionsProvider(nationStates, regionDumpAccess, input);

            case NATIONS_IN_REGIONS_WITH_TAGS: {
                var regionTags = regionTagStringsToEnums(input);
                return new NationsInRegionsWithTagsProvider(nationStates, regionDumpAccess, regionTags);
            }

            case NATIONS_IN_REGIONS_WITHOUT_TAGS: {
                var regionTags = regionTagStringsToEnums(input);
                return new NationsInRegionsWithoutTagsProvider(nationStates, regionDumpAccess, regionTags);
            }

            case NATIONS_IN_REGIONS:
                return new NationsInRegionsProvider(nationStates, input);

            case NEW_NATIONS:
                return new NewNationsProvider(nationStates);

            case NEW_DELEGATES:
                return new NewDelegatesProvider(nationStates);

            case NEW_WORLD_ASSEMBLY_MEMBERS:
                return new NewWorldAssemblyMembersProvider(nationStates);

            case REFOUNDED_NATIONS:
                return new RefoundedNationsProvider(nationStates);

            case NATIONS:
                return new NationsProvider(input);

            case DELEGATES:
                return new DelegatesProvider(nationStates);

            case WORLD_ASSEMBLY_MEMBERS:
                return new WorldAssemblyMembersProvider(nationStates);

            default:
                return new NullRecipientsProvider();
        }
    }

    private Collection<String> normalizeNames(Collection<String> names) {
        var normalized = new HashSet<String>();

        if (names != null) {
            names.forEach(name -> {
                normalized.add(StringFunctions.normalizeName(name));
            });
        }
        return normalized;
    }

    private Collection<RegionTag> regionTagStringsToEnums(Collection<String> regionTags) {
        var enums = new HashSet<RegionTag>();

        regionTags.forEach((regionTag) -> {
            try {
                enums.add(RegionTag.fromString(regionTag));
            } catch (IllegalArgumentException ex) {
                log.error("Stumbled upon unknown region tag", ex);
            }
        });
        return enums;
    }
}
