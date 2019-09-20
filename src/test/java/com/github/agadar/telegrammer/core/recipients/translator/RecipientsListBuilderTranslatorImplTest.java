package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.nationstates.NationStatesMock;
import com.github.agadar.telegrammer.core.recipients.filter.NullRecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterWithProvider;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
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
        final RecipientsListBuilder builder = builderTranslator
                .toBuilder("[\"ADD_TO_RECIPIENTS.NATIONS[agadar, vancouvia]\"]");

        // Assert
        assertNotNull(builder);
        assertNotNull(builder.getFilters());
        assertEquals(1, builder.getFilters().size());

        assertTrue(builder.getFilters().get(0) instanceof RecipientsFilterWithProvider);
        final RecipientsFilterWithProvider filter = (RecipientsFilterWithProvider) builder.getFilters().get(0);
        assertEquals(RecipientsFilterAction.ADD_TO_RECIPIENTS, filter.getFilterAction());
        assertTrue(filter.getRecipientsProvider() instanceof NationsProvider);
        var recipients = filter.getRecipientsProvider().getRecipients();
        assertEquals(2, recipients.size());
        assertTrue(recipients.contains("agadar"));
        assertTrue(recipients.contains("vancouvia"));
    }

    @Test
    public void testToBuilder_valid2() {
        System.out.println("toBuilder should create an IRecipientsListBuilder with 2+ filters on valid string");

        // Act
        final RecipientsListBuilder builder = builderTranslator.toBuilder(
                "[\"ADD_TO_RECIPIENTS.NATIONS[agadar, vancouvia]\", \"REMOVE_FROM_RECIPIENTS.NATIONS[agadar, vancouvia]\"]");

        // Assert
        assertNotNull(builder);
        assertNotNull(builder.getFilters());
        assertEquals(2, builder.getFilters().size());

        assertTrue(builder.getFilters().get(0) instanceof RecipientsFilterWithProvider);
        final RecipientsFilterWithProvider filter = (RecipientsFilterWithProvider) builder.getFilters().get(0);
        assertEquals(RecipientsFilterAction.ADD_TO_RECIPIENTS, filter.getFilterAction());
        assertTrue(filter.getRecipientsProvider() instanceof NationsProvider);
        var recipients = filter.getRecipientsProvider().getRecipients();
        assertEquals(2, recipients.size());
        assertTrue(recipients.contains("agadar"));
        assertTrue(recipients.contains("vancouvia"));

        assertTrue(builder.getFilters().get(1) instanceof RecipientsFilterWithProvider);
        final RecipientsFilterWithProvider filter2 = (RecipientsFilterWithProvider) builder.getFilters().get(1);
        assertEquals(RecipientsFilterAction.REMOVE_FROM_RECIPIENTS, filter2.getFilterAction());
        assertTrue(filter2.getRecipientsProvider() instanceof NationsProvider);
        var recipients2 = filter2.getRecipientsProvider().getRecipients();
        assertEquals(2, recipients2.size());
        assertTrue(recipients2.contains("agadar"));
        assertTrue(recipients2.contains("vancouvia"));
    }
}
