package com.github.agadar.telegrammer.core.recipients.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertTrue(recipientsFilter.getRecipientsProvider().getRecipients().isEmpty());
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
        assertTrue(recipientsFilter.getRecipientsProvider().getRecipients().isEmpty());
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
        assertEquals(1, recipientsFilter.getRecipientsProvider().getRecipients().size());
        assertTrue(recipientsFilter.getRecipientsProvider().getRecipients().contains("agadar"));
    }

    @Test
    public void testToFilter_valid2() {
        System.out.println("toFilter should return RecipientsFilter on valid string");

        // Act
        final RecipientsFilter filter = filterTranslator.toFilter("ADD_TO_RECIPIENTS.NATIONS[agadar, vancouvia]");

        // Assert
        assertTrue(filter instanceof RecipientsFilterWithProvider);
        final RecipientsFilterWithProvider recipientsFilter = (RecipientsFilterWithProvider) filter;
        assertEquals(recipientsFilter.getFilterAction(), RecipientsFilterAction.ADD_TO_RECIPIENTS);
        assertTrue(recipientsFilter.getRecipientsProvider() instanceof NationsProvider);
        var recipients = recipientsFilter.getRecipientsProvider().getRecipients();
        assertEquals(2, recipients.size());
        assertTrue(recipients.contains("agadar"));
        assertTrue(recipients.contains("vancouvia"));
    }
}
