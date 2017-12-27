package com.github.agadar.telegrammer.core.util;

import com.github.agadar.nationstates.domain.common.Happening;
import com.github.agadar.nationstates.enumerator.RegionTag;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Exposes some String-related utility functions.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class StringFunctions {

    /**
     * The keyword used for finding the correct happenings in a list of
     * happenings.
     */
    public static enum KeyWord {
        became, // new delegates
        refounded, // refounded nations
        ejected, // ejected nations
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
     * Converts a comma-separated string to a list of strings.
     *
     * @param string
     * @return
     */
    public static Set<String> stringToStringList(String string) {
        final List<String> asList = Arrays.asList(string.trim().split("\\s*,\\s*"));
        return asList.size() == 1 && asList.get(0).isEmpty() ? new HashSet<>() : new HashSet<>(asList);
    }

    /**
     * Parses the supplied string values to a set of RegionTags. Strings that
     * cannot be parsed are ignored.
     *
     * @param tagsStrSet The strings to parse
     * @return The resulting RegionTags
     */
    public static Set<RegionTag> stringsToRegionTags(Collection<String> tagsStrSet) {
        final Set<RegionTag> tags = new HashSet();
        tagsStrSet.stream().forEach(tagStr -> {
            try {
                tags.add(RegionTag.fromString(tagStr));
            } catch (IllegalArgumentException ex) {
                // Ignore because we don't care.
            }
        });
        return tags;
    }

    /**
     * Parses the supplied string to an unsigned int. If the supplied string is
     * null or cannot be parsed, then 0 is returned.
     *
     * @param parseMe
     * @return
     */
    public static int stringToUInt(String parseMe) {
        if (parseMe == null) {
            return 0;
        }

        try {
            return Integer.parseUnsignedInt(parseMe);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * From a list of happenings, extracts the nation name from each happening
     * that contains in its description the supplied keyword.
     *
     * @param happenings
     * @param keyword
     * @return Nation names
     */
    public static HashSet<String> extractNationsFromHappenings(Collection<Happening> happenings, KeyWord keyword) {
        final HashSet<String> nationNames = new HashSet<>();

        happenings.forEach(happening -> {
            if (happening.description.contains(keyword.toString())) {
                final Matcher matcher = PATTERN.matcher(happening.description);

                if (matcher.find()) {
                    nationNames.add(matcher.group(1));
                }
            }
        });
        return nationNames;
    }
}
