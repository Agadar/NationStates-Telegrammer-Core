package com.github.agadar.telegrammer.core.util;

import java.util.ArrayList;
import java.util.HashSet;

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
        var regionTagStrings = new ArrayList<String>();
        regionTagStrings.add("anti-capitalist");
        regionTagStrings.add("casual");
        regionTagStrings.add("non-existent-tag-4765728462");

        // Act
        var regionTags = StringFunctions.stringsToRegionTags(regionTagStrings);

        // Assert
        Assert.assertEquals(2, regionTags.size());
        Assert.assertTrue(regionTags.contains(RegionTag.ANTI_CAPITALIST));
        Assert.assertTrue(regionTags.contains(RegionTag.CASUAL));

    }

    @Test
    public void testExtractNationsFromHappenings() {
        System.out.println("testExtractNationsFromHappenings");

        // Arrange
        var happenings = new HashSet<Happening>();
        happenings.add(new Happening(193257689L, 1520092742L, "@@ninetang@@ applied to join the World Assembly."));
        happenings.add(new Happening(193257689L, 1520092742L, "@@tientang@@ was admitted to the World Assembly."));

        // Act
        var nations = StringFunctions.extractNationsFromHappenings(happenings, KeyWord.admitted);

        // Assert
        Assert.assertEquals(1, nations.size());
        Assert.assertEquals("tientang", nations.get(0));
    }

}
