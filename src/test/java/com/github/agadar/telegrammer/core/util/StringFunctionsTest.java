package com.github.agadar.telegrammer.core.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.github.agadar.nationstates.domain.common.happening.Happening;
import com.github.agadar.nationstates.enumerator.RegionTag;
import com.github.agadar.telegrammer.core.util.StringFunctions.KeyWord;

/**
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class StringFunctionsTest {

    @Test
    public void testStringsToRegionTags() {
        System.out.println("testStringsToRegionTags");

        // Arrange
        final Set<RegionTag> expectedRegionTags = new HashSet<RegionTag>();
        expectedRegionTags.add(RegionTag.ANTI_CAPITALIST);
        expectedRegionTags.add(RegionTag.CASUAL);

        final Set<String> regionTagStrings = new HashSet<String>();
        regionTagStrings.add("anti-capitalist");
        regionTagStrings.add("casual");
        regionTagStrings.add("non-existent-tag-4765728462");

        // Act
        final Set<RegionTag> regionTags = StringFunctions.stringsToRegionTags(regionTagStrings);

        // Assert
        Assert.assertTrue(Objects.deepEquals(regionTags, expectedRegionTags));

    }

    @Test
    public void testExtractNationsFromHappenings() {
        System.out.println("testExtractNationsFromHappenings");

        // Arrange
        final Set<String> expectedNations = new HashSet<String>();
        expectedNations.add("tientang");

        final Set<Happening> happenings = new HashSet<Happening>();
        happenings.add(new Happening(193257689L, 1520092742L, "@@ninetang@@ applied to join the World Assembly."));
        happenings.add(new Happening(193257689L, 1520092742L, "@@tientang@@ was admitted to the World Assembly."));

        // Act
        final Set<String> nations = StringFunctions.extractNationsFromHappenings(happenings, KeyWord.admitted);

        // Assert
        Assert.assertTrue(Objects.deepEquals(expectedNations, nations));
    }

}
