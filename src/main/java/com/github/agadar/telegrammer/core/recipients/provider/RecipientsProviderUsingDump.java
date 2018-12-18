package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccess;

import lombok.NonNull;

/**
 * Defines usage of IRegionDumpAccess to child providers.
 * 
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class RecipientsProviderUsingDump extends NationStatesRecipientsProvider {

    protected final RegionDumpAccess regionDumpAccess;
    
    public RecipientsProviderUsingDump(@NonNull NationStates nationStates, @NonNull RegionDumpAccess regionDumpAccess) {
        super(nationStates);
        this.regionDumpAccess = regionDumpAccess;
    }

}
