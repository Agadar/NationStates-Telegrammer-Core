package com.github.agadar.telegrammer.core.recipients.filter;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Test;

import com.github.agadar.telegrammer.core.recipients.provider.NationsProvider;

/**
 * Tests {@link RecipientsFilterWithProvider}.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class RecipientsFilterWithProviderTest {

    @Test
    public void testToConfigurationString() {
        System.out.println("toConfigurationString should return correct string");

        // Arrange
        var nations = new HashSet<String>();
        nations.add("agadar");
        nations.add("vancouvia");
        var provider = new NationsProvider(nations);
        var filter = new RecipientsFilterWithProvider(provider, RecipientsFilterAction.ADD_TO_RECIPIENTS);

        // Act
        String stringified = filter.toConfigurationString();

        // Assert
        assertEquals("ADD_TO_RECIPIENTS.NATIONS[agadar, vancouvia]", stringified);
    }
}
