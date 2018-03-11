package com.github.agadar.telegrammer.core.recipients.provider;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.telegrammer.core.regiondumpaccess.IRegionDumpAccess;

/**
 * Defines usage of INationDumpAccess to child providers.
 * 
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class RecipientsProviderUsingDump extends RecipientsProvider {

    protected final IRegionDumpAccess nationDumpAccess;
    
    public RecipientsProviderUsingDump(INationStates nationStates, IRegionDumpAccess nationDumpAccess) {
        super(nationStates);
        this.nationDumpAccess = nationDumpAccess;
    }

}
