package com.github.agadar.nationstates;

import java.util.function.Predicate;

import com.github.agadar.nationstates.domain.nation.Nation;
import com.github.agadar.nationstates.domain.region.Region;
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
public class NationStatesMock implements NationStates {

    @Override
    public void setUserAgent(String userAgent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void doVersionCheck() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerTypes(@SuppressWarnings("rawtypes") Class... types) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NationQuery getNation(String nationName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RegionQuery getRegion(String regionName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WorldQuery getWorld(WorldShard... shards) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WorldAssemblyQuery getWorldAssembly(Council council) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public VersionQuery getVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public VerifyQuery verifyNation(String nation, String checksum) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TelegramQuery sendTelegrams(String clientKey, String telegramId, String secretKey, String... nations) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RegionDumpQuery getRegionDump(DailyDumpMode mode, Predicate<Region> filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NationDumpQuery getNationDump(DailyDumpMode mode, Predicate<Nation> filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
