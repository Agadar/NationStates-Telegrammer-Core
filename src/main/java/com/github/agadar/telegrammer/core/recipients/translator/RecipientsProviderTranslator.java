package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.enumerator.RegionTag;

import com.github.agadar.telegrammer.core.nationdumpaccess.INationDumpAccess;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.recipients.provider.AllNationsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.DelegatesProvider;
import com.github.agadar.telegrammer.core.recipients.provider.EjectedNationsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.IRecipientsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NationsInEmbassyRegionsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NationsInRegionsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NationsInRegionsWithTagsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NationsInRegionsWithoutTagsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NationsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NewDelegatesProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NewNationsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NewWorldAssemblyMembersProvider;
import com.github.agadar.telegrammer.core.recipients.provider.RefoundedNationsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.WorldAssemblyMembersProvider;

import java.util.HashSet;

public class RecipientsProviderTranslator implements IRecipientsProviderTranslator {

    private final INationStates nationStates;
    private final INationDumpAccess nationDumpAccess;

    public RecipientsProviderTranslator(INationStates nationStates, INationDumpAccess nationDumpAccess) {
        this.nationStates = nationStates;
        this.nationDumpAccess = nationDumpAccess;
    }

    @Override
    public IRecipientsProvider toProvider(RecipientsProviderType providerType, HashSet<String> input) {
        switch (providerType) {
            case ALL_NATIONS:
                return new AllNationsProvider(nationStates);
            case EJECTED_NATIONS:
                return new EjectedNationsProvider(nationStates);
            case NATIONS_IN_EMBASSY_REGIONS:
                return new NationsInEmbassyRegionsProvider(nationStates, nationDumpAccess, input);
            case NATIONS_IN_REGIONS_WITH_TAGS: {
                final HashSet<RegionTag> regionTags = regionTagStringsToEnums(input);
                return new NationsInRegionsWithTagsProvider(nationStates, nationDumpAccess, regionTags);
            }
            case NATIONS_IN_REGIONS_WITHOUT_TAGS: {
                final HashSet<RegionTag> regionTags = regionTagStringsToEnums(input);
                return new NationsInRegionsWithoutTagsProvider(nationStates, nationDumpAccess, regionTags);
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
                return null;
        }
    }

    private HashSet<RegionTag> regionTagStringsToEnums(HashSet<String> regionTags) {
        final HashSet<RegionTag> enums = new HashSet<>();

        regionTags.forEach((regionTag) -> {
            try {
                enums.add(RegionTag.fromString(regionTag));
            } catch (IllegalArgumentException err) {
                // Silent ignore.
            }
        });
        return enums;
    }

}
