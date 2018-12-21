package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.nationstates.NationStatesMock;
import com.github.agadar.telegrammer.core.recipients.filter.NullRecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterImpl;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.recipients.listbuilder.RecipientsListBuilder;
import com.github.agadar.telegrammer.core.recipients.provider.NationsProvider;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccessMock;
import com.github.agadar.telegrammer.core.telegram.history.TelegramHistoryMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RecipientsListBuilderTranslatorImplTest {

    private RecipientsListBuilderTranslatorImpl builderTranslator;

    private TelegramHistoryMock telegramHistory;
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
        telegramHistory = new TelegramHistoryMock();
        builderTranslator = new RecipientsListBuilderTranslatorImpl(telegramHistory, filterTranslator);
    }

    @After
    public void tearDown() {
        nationStatesMock = null;
        accessMock = null;
        recipientsTranslator = null;
        filterTranslator = null;
        nationStatesMock = null;
        builderTranslator = null;
    }

    @Test
    public void testToBuilder_null() {
        System.out.println("toBuilder should create an empty IRecipientsListBuilder on null");

        // Act
        final RecipientsListBuilder builder = builderTranslator.toBuilder(null);

        // Assert
        assertNotNull(builder);
        assertNotNull(builder.getFilters());
        assertEquals(0, builder.getFilters().size());
    }

    @Test
    public void testToBuilder_empty() {
        System.out.println("toBuilder should create an empty IRecipientsListBuilder on empty string");

        // Act
        final RecipientsListBuilder builder = builderTranslator.toBuilder("");

        // Assert
        assertNotNull(builder);
        assertNotNull(builder.getFilters());
        assertEquals(0, builder.getFilters().size());
    }

    @Test
    public void testToBuilder_invalid() {
        System.out.println("toBuilder should create an IRecipientsListBuilder with 1 null-filter on invalid string");

        // Act
        final RecipientsListBuilder builder = builderTranslator.toBuilder("invalidString");

        // Assert
        assertNotNull(builder);
        assertNotNull(builder.getFilters());
        assertEquals(1, builder.getFilters().size());
        assertTrue(builder.getFilters().get(0) instanceof NullRecipientsFilter);
    }

    @Test
    public void testToBuilder_valid() {
        System.out.println("toBuilder should create an IRecipientsListBuilder with 1 filter on valid string");

        // Act
        final RecipientsListBuilder builder = builderTranslator.toBuilder("[\"ADD_TO_RECIPIENTS.NATIONS[agadar, vancouvia]\"]");

        // Assert
        assertNotNull(builder);
        assertNotNull(builder.getFilters());
        assertEquals(1, builder.getFilters().size());

        assertTrue(builder.getFilters().get(0) instanceof RecipientsFilterImpl);
        final RecipientsFilterImpl filter = (RecipientsFilterImpl) builder.getFilters().get(0);
        assertEquals(RecipientsFilterType.ADD_TO_RECIPIENTS, filter.filterType);
        assertTrue(filter.recipientsProvider instanceof NationsProvider);
        assertEquals("[agadar, vancouvia]", ((NationsProvider) filter.recipientsProvider).nations.toString());
    }

    @Test
    public void testToBuilder_valid2() {
        System.out.println("toBuilder should create an IRecipientsListBuilder with 2+ filters on valid string");

        // Act
        final RecipientsListBuilder builder = builderTranslator.toBuilder("[\"ADD_TO_RECIPIENTS.NATIONS[agadar, vancouvia]\", \"REMOVE_FROM_RECIPIENTS.NATIONS[agadar, vancouvia]\"]");

        // Assert
        assertNotNull(builder);
        assertNotNull(builder.getFilters());
        assertEquals(2, builder.getFilters().size());

        assertTrue(builder.getFilters().get(0) instanceof RecipientsFilterImpl);
        final RecipientsFilterImpl filter = (RecipientsFilterImpl) builder.getFilters().get(0);
        assertEquals(RecipientsFilterType.ADD_TO_RECIPIENTS, filter.filterType);
        assertTrue(filter.recipientsProvider instanceof NationsProvider);
        assertEquals("[agadar, vancouvia]", ((NationsProvider) filter.recipientsProvider).nations.toString());

        assertTrue(builder.getFilters().get(1) instanceof RecipientsFilterImpl);
        final RecipientsFilterImpl filter2 = (RecipientsFilterImpl) builder.getFilters().get(1);
        assertEquals(RecipientsFilterType.REMOVE_FROM_RECIPIENTS, filter2.filterType);
        assertTrue(filter2.recipientsProvider instanceof NationsProvider);
        assertEquals("[agadar, vancouvia]", ((NationsProvider) filter2.recipientsProvider).nations.toString());
    }
}