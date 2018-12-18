package com.github.agadar.telegrammer.core.regiondumpaccess;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.region.Region;
import com.github.agadar.nationstates.enumerator.DailyDumpMode;
import com.github.agadar.nationstates.query.RegionDumpQuery;

/**
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RegionDumpAccessImplTest {

    private final RegionDumpQuery query = Mockito.mock(RegionDumpQuery.class);
    private final NationStates nationStates = Mockito.mock(NationStates.class);
    private final RegionDumpAccessImpl regionDumpAccess = new RegionDumpAccessImpl(nationStates);

    @SuppressWarnings("unchecked")
    @Test
    public void getNationsInRegions() {
        System.out.println("getNationsInRegions");

        // Arrange
        final Set<String> expected = Sets.newSet("agadar", "vancouvia", "nights_edge");

        final Region region1 = new Region();
        final Region region2 = new Region();
        final Region region3 = new Region();
        region1.setName("The Western Isles");
        region2.setName("The Eastern Isles");
        region3.setName("The Northern Isles");
        region1.setNationNames(Sets.newSet("Agadar", "Vancouvia"));
        region2.setNationNames(Sets.newSet("Nights_Edge"));
        region3.setNationNames(Sets.newSet("Atnaia", "Polar_Svalbard"));
        final Set<Region> regions = Sets.newSet(region1, region2, region3);

        Mockito.when(nationStates.getRegionDump(Mockito.any(DailyDumpMode.class), Mockito.any(Predicate.class)))
                .thenReturn(query);
        Mockito.when(query.execute()).thenReturn(regions);

        // Act
        var actual = regionDumpAccess
                .getNationsInRegions(Sets.newSet("The Western Isles", "The Eastern Isles"));

        // Assert
        Assert.assertTrue(Objects.deepEquals(expected, actual));
    }

}
