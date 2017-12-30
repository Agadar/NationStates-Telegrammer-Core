package com.github.agadar.nationstates;

import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.enumerator.DailyDumpMode;
import com.github.agadar.nationstates.query.NationDumpQuery;
import com.github.agadar.nationstates.query.NationQuery;
import com.github.agadar.nationstates.query.RegionDumpQuery;
import com.github.agadar.nationstates.query.RegionQuery;
import com.github.agadar.nationstates.query.TelegramQuery;
import com.github.agadar.nationstates.query.VerifyQuery;
import com.github.agadar.nationstates.query.VersionQuery;
import com.github.agadar.nationstates.query.WorldAssemblyQuery;
import com.github.agadar.nationstates.query.WorldQuery;
import com.github.agadar.nationstates.shard.WorldShard;

/**
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NationStatesMock implements INationStates {

    @Override
    public void setUserAgent(String userAgent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doVersionCheck() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void registerTypes(Class... types) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NationQuery getNation(String nationName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RegionQuery getRegion(String regionName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public WorldQuery getWorld(WorldShard... shards) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public WorldAssemblyQuery getWorldAssembly(Council council) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VersionQuery getVersion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VerifyQuery verifyNation(String nation, String checksum) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TelegramQuery sendTelegrams(String clientKey, String telegramId, String secretKey, String... nations) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RegionDumpQuery getRegionDump(DailyDumpMode mode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NationDumpQuery getNationDump(DailyDumpMode mode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
