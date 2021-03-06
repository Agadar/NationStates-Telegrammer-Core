package com.github.agadar.telegrammer.core.recipients.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link RecipientsWithNumbersFilter}.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class RecipientsWithNumbersFilterTest {

    private RecipientsFilter filter;
    private Collection<String> recipients;

    @Before
    public void initialise() {
        filter = new RecipientsWithNumbersFilter();
        recipients = new HashSet<String>();
        recipients.add("agadar");
        recipients.add("agadar1");
        recipients.add("vancouvia");
        recipients.add("2vancouvia");
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
        assertEquals("(-) Nations with numbers in name", stringified);
    }

    @Test
    public void testToConfigurationString() {

        // Act
        String stringified = filter.toConfigurationString();

        // Assert
        assertEquals("REMOVE_FROM_RECIPIENTS.NATIONS_WITH_NUMBERS", stringified);
    }
}
