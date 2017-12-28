package com.github.agadar.telegrammer.core.util;

import com.github.agadar.nationstates.domain.common.Happening;
import com.github.agadar.nationstates.enumerator.RegionTag;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
    public static HashSet<String> stringToStringList(String string) {
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
    public static HashSet<RegionTag> stringsToRegionTags(Collection<String> tagsStrSet) {
        final HashSet<RegionTag> tags = new HashSet();
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

    /**
     * Normalizes a (region/nation) name, meaning it's lowercased and spaces are
     * replaced by underscores. Required for dump files because apparently those
     * are not normalized in this way, unlike everything else returned by the
     * API.
     *
     * @param name
     * @return
     */
    public static String normalizeName(String name) {
        String replace = name.replace(' ', '_');
        replace = replace.toLowerCase();
        return replace;
    }
}
