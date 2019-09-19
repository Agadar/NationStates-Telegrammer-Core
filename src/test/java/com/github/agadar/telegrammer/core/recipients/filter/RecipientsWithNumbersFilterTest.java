package com.github.agadar.telegrammer.core.recipients.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    private RecipientsWithNumbersFilter filter;

    @Before
    public void before() {
        filter = new RecipientsWithNumbersFilter();
    }

    @Test
    public void testApplyFilterToRecipients() {

        // Arrange
        var recipients = new HashSet<String>();
        recipients.add("agadar");
        recipients.add("agadar1");
        recipients.add("vancouvia");
        recipients.add("9vancouvia");

        // Act
        filter.applyFilterToRecipients(recipients);

        // Assert
        assertEquals(2, recipients.size());
        assertTrue(recipients.contains("agadar"));
        assertTrue(recipients.contains("vancouvia"));
    }
}
