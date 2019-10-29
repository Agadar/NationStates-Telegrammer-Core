package com.github.agadar.telegrammer.core.recipients.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link RecipientsContainingKeywordsFilter}.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class RecipientsContainingKeywordsFilterTest {

    private RecipientsFilter filter;
    private Collection<String> recipients;

    @Before
    public void initialise() {
        var keywords = Arrays.asList("noob", "scrub", "l2p");
        filter = new RecipientsContainingKeywordsFilter(keywords);

        recipients = new HashSet<String>();
        recipients.add("agadar");
        recipients.add("agadarnoob");
        recipients.add("vancouvia");
        recipients.add("scrubvancouvia");
    }

    @Test
    public void testApplyFilterToRecipients() {

        // Act
        filter.applyFilterToRecipients(recipients);

        // Assert
        assertEquals(2, recipients.size());
        assertTrue(recipients.contains("agadar"));
        assertTrue(recipients.contains("vancouvia"));
    }

    @Test
    public void testToString() {

        // Act
        String stringified = filter.toString();

        // Assert
        assertEquals("(-) Nations containing specific keywords [noob, scrub, l2p]", stringified);
    }

    @Test
    public void testToConfigurationString() {

        // Act
        String stringified = filter.toConfigurationString();

        // Assert
        assertEquals("REMOVE_FROM_RECIPIENTS.NATIONS_CONTAINING_KEYWORDS[noob, scrub, l2p]", stringified);
    }
}
