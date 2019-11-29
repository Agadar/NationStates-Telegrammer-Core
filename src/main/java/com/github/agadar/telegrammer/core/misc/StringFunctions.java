package com.github.agadar.telegrammer.core.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.domain.common.happening.Happening;
import com.github.agadar.nationstates.enumerator.RegionTag;

/**
 * Exposes some String-related utility functions.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class StringFunctions {

    /**
     * The keyword used for finding the correct happenings in a list of happenings.
     */
    public static enum KeyWord {
        became, // new delegates
        refounded, // refounded nations
        admitted // new WA members
        ;
    }

    /**
     * Pattern used for extracting nation names from happenings descriptions.
     */
    private final static Pattern PATTERN = Pattern.compile("\\@\\@(.*?)\\@\\@");

    /**
     * Private constructor.
     */
    private StringFunctions() {
    }

    /**
     * Converts a comma-separated string to a collection of strings.
     *
     * @param string
     * @return
     */
    public static Collection<String> stringToHashSet(String string) {
        if (string == null || string.isEmpty()) {
            return new HashSet<String>();
        }
        var asList = Arrays.asList(string.trim().split("\\s*,\\s*"));
        return asList.size() == 1 && asList.get(0).isEmpty() ? new HashSet<String>() : new HashSet<>(asList);
    }

    /**
     * Converts a comma-separated string to an arraylist of strings.
     *
     * @param string
     * @return
     */
    public static ArrayList<String> stringToArrayList(String string) {
        if (string == null || string.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(string.trim().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)));
    }

    /**
     * Parses the supplied string values to a set of RegionTags. Strings that cannot
     * be parsed are ignored.
     *
     * @param tagsStrSet The strings to parse
     * @return The resulting RegionTags
     */
    public static Collection<RegionTag> stringsToRegionTags(Collection<String> tagsStrSet) {
        return tagsStrSet.stream().map((tagStr) -> RegionTag.fromString(tagStr)).filter(tag -> tag != RegionTag.NULL)
                .collect(Collectors.toList());
    }

    /**
     * From a list of happenings, extracts the nation name from each happening that
     * contains in its description the supplied keyword.
     *
     * @param happenings
     * @param keyword
     * @return Nation names
     */
    public static List<String> extractNationsFromHappenings(Collection<Happening> happenings, KeyWord keyword) {
        return happenings.stream()
                .filter(happening -> happening.getDescription().contains(keyword.toString()))
                .map(happening -> PATTERN.matcher(happening.getDescription()))
                .filter(matcher -> matcher.find())
                .map(matcher -> matcher.group(1)).collect(Collectors.toList());
    }

    /**
     * Normalizes a (region/nation) name, meaning it's lowercased and spaces are
     * replaced by underscores. Required for dump files because apparently those are
     * not normalized in this way, unlike everything else returned by the API.
     *
     * @param name
     * @return
     */
    public static String normalizeName(String name) {
        return name.replace(' ', '_').toLowerCase();
    }
}
