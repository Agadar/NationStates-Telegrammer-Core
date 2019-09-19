package com.github.agadar.telegrammer.core.recipients.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.agadar.nationstates.NationStatesMock;
import com.github.agadar.telegrammer.core.recipients.filter.NullRecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterWithProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NationsProvider;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccessMock;

/**
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RecipientsFilterTranslatorImplTest {

    private RecipientsFilterTranslatorImpl filterTranslator;

    private RecipientsProviderTranslatorImpl recipientsTranslator;
    private NationStatesMock nationStatesMock;
    private RegionDumpAccessMock accessMock;

    @Before
    public void setUp() {
        nationStatesMock = new NationStatesMock();
        accessMock = new RegionDumpAccessMock();
        recipientsTranslator = new RecipientsProviderTranslatorImpl(nationStatesMock, accessMock);
        filterTranslator = new RecipientsFilterTranslatorImpl(recipientsTranslator);
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
        final RecipientsFilter filter = filterTranslator.toFilter(null);

        // Assert
        assertTrue(filter instanceof NullRecipientsFilter);
    }

    @Test
    public void testToFilter_empty() {
        System.out.println("toFilter should return null-filter on empty string");

        // Act
        final RecipientsFilter filter = filterTranslator.toFilter("");

        // Assert
        assertTrue(filter instanceof NullRecipientsFilter);
    }

    @Test
    public void testToFilter_invalid() {
        System.out.println("toFilter should return null-filter on invalid string");

        // Act
        final RecipientsFilter filter = filterTranslator.toFilter("invalidString");

        // Assert
        assertTrue(filter instanceof NullRecipientsFilter);
    }

    @Test
    public void testToProvider_emptyNations() {
        System.out.println("toFilter should return empty NationsProvider on 'ADD_TO_RECIPIENTS.NATIONS'");

        // Act
        var filter = filterTranslator.toFilter("ADD_TO_RECIPIENTS.NATIONS");

        // Assert
        assertTrue(filter instanceof RecipientsFilterWithProvider);
        var recipientsFilter = (RecipientsFilterWithProvider) filter;
        assertEquals(recipientsFilter.getFilterAction(), RecipientsFilterAction.ADD_TO_RECIPIENTS);
        assertTrue(recipientsFilter.getRecipientsProvider() instanceof NationsProvider);
        assertEquals(((NationsProvider) recipientsFilter.getRecipientsProvider()).nations.toString(), "[]");
    }

    @Test
    public void testToProvider_emptyNations2() {
        System.out.println("toFilter should return empty NationsProvider on 'ADD_TO_RECIPIENTS.NATIONS[]'");

        // Act
        var filter = filterTranslator.toFilter("ADD_TO_RECIPIENTS.NATIONS[]");

        // Assert
        assertTrue(filter instanceof RecipientsFilterWithProvider);
        var recipientsFilter = (RecipientsFilterWithProvider) filter;
        assertEquals(recipientsFilter.getFilterAction(), RecipientsFilterAction.ADD_TO_RECIPIENTS);
        assertTrue(recipientsFilter.getRecipientsProvider() instanceof NationsProvider);
        assertEquals(((NationsProvider) recipientsFilter.getRecipientsProvider()).nations.toString(), "[]");
    }

    @Test
    public void testToFilter_valid() {
        System.out.println("toFilter should return RecipientsFilter on valid string");

        // Act
        final RecipientsFilter filter = filterTranslator.toFilter("ADD_TO_RECIPIENTS.NATIONS[agadar]");

        // Assert
        assertTrue(filter instanceof RecipientsFilterWithProvider);
        final RecipientsFilterWithProvider recipientsFilter = (RecipientsFilterWithProvider) filter;
        assertEquals(recipientsFilter.getFilterAction(), RecipientsFilterAction.ADD_TO_RECIPIENTS);
        assertTrue(recipientsFilter.getRecipientsProvider() instanceof NationsProvider);
        assertEquals(((NationsProvider) recipientsFilter.getRecipientsProvider()).nations.toString(), "[agadar]");
    }

    @Test
    public void testToFilter_valid2() {
        System.out.println("toFilter should return RecipientsFilter on valid string");

        // Act
        final RecipientsFilter filter = filterTranslator.toFilter("ADD_TO_RECIPIENTS.NATIONS[agadar,vancouvia]");

        // Assert
        assertTrue(filter instanceof RecipientsFilterWithProvider);
        final RecipientsFilterWithProvider recipientsFilter = (RecipientsFilterWithProvider) filter;
        assertEquals(recipientsFilter.getFilterAction(), RecipientsFilterAction.ADD_TO_RECIPIENTS);
        assertTrue(recipientsFilter.getRecipientsProvider() instanceof NationsProvider);
        assertEquals(((NationsProvider) recipientsFilter.getRecipientsProvider()).nations.toString(),
                "[agadar, vancouvia]");
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
        final RecipientsFilterWithProvider filter = new RecipientsFilterWithProvider(provider,
                RecipientsFilterAction.ADD_TO_RECIPIENTS);

        // Act
        final String stringified = filterTranslator.fromFilter(filter);

        // Assert
        assertEquals("ADD_TO_RECIPIENTS.NATIONS[agadar]", stringified);
    }

}
