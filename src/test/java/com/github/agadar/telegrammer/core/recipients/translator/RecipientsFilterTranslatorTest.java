package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.nationstates.NationStatesMock;
import com.github.agadar.telegrammer.core.nationdumpaccess.NationDumpAccessMock;
import com.github.agadar.telegrammer.core.recipients.filter.IRecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.NullRecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.recipients.provider.NationsProvider;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RecipientsFilterTranslatorTest {

    private RecipientsFilterTranslator filterTranslator;

    private RecipientsProviderTranslator recipientsTranslator;
    private NationStatesMock nationStatesMock;
    private NationDumpAccessMock accessMock;

    @Before
    public void setUp() {
        nationStatesMock = new NationStatesMock();
        accessMock = new NationDumpAccessMock();
        recipientsTranslator = new RecipientsProviderTranslator(nationStatesMock, accessMock);
        filterTranslator = new RecipientsFilterTranslator(recipientsTranslator);
    }

    @After
    public void tearDown() {
        nationStatesMock = null;
        accessMock = null;
        recipientsTranslator = null;
        filterTranslator = null;
    }

    @Test
    public void testToFilter_null() {
        System.out.println("toFilter should return null-filter on null string");

        // Act
        final IRecipientsFilter filter = filterTranslator.toFilter(null);

        // Assert
        assertTrue(filter instanceof NullRecipientsFilter);
    }

    @Test
    public void testToFilter_empty() {
        System.out.println("toFilter should return null-filter on empty string");

        // Act
        final IRecipientsFilter filter = filterTranslator.toFilter("");

        // Assert
        assertTrue(filter instanceof NullRecipientsFilter);
    }

    @Test
    public void testToFilter_invalid() {
        System.out.println("toFilter should return null-filter on invalid string");

        // Act
        final IRecipientsFilter filter = filterTranslator.toFilter("invalidString");

        // Assert
        assertTrue(filter instanceof NullRecipientsFilter);
    }

    @Test
    public void testToFilter_valid() {
        System.out.println("toFilter should return RecipientsFilter on valid string");

        // Act
        final IRecipientsFilter filter = filterTranslator.toFilter("ADD_TO_RECIPIENTS.NATIONS[agadar]");

        // Assert
        assertTrue(filter instanceof RecipientsFilter);
        final RecipientsFilter recipientsFilter = (RecipientsFilter) filter;
        assertEquals(recipientsFilter.filterType, RecipientsFilterType.ADD_TO_RECIPIENTS);
        assertTrue(recipientsFilter.recipientsProvider instanceof NationsProvider);
        assertEquals(((NationsProvider) recipientsFilter.recipientsProvider).nations.toString(), "[agadar]");
    }

    @Test
    public void testFromFilter_null() {
        System.out.println("fromFilter should return empty string on null");

        // Act
        final String stringified = filterTranslator.fromFilter(null);

        // Assert
        assertEquals("", stringified);
    }

    @Test
    public void testFromFilter_nullfilter() {
        System.out.println("fromFilter should return empty string on null-filter");

        // Act
        final String stringified = filterTranslator.fromFilter(new NullRecipientsFilter());

        // Assert
        assertEquals("", stringified);
    }

    @Test
    public void testFromFilter_filter() {
        System.out.println("fromFilter should return correct string on RecipientsFilter");

        // Arrange
        final HashSet<String> nations = new HashSet<>();
        nations.add("agadar");
        final NationsProvider provider = new NationsProvider(nations);
        final RecipientsFilter filter = new RecipientsFilter(provider, RecipientsFilterType.ADD_TO_RECIPIENTS);

        // Act
        final String stringified = filterTranslator.fromFilter(filter);

        // Assert
        assertEquals("ADD_TO_RECIPIENTS.NATIONS[agadar]", stringified);
    }

}
