package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.nationstates.NationStatesMock;
import com.github.agadar.telegrammer.core.recipients.provider.RecipientsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NationsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NullRecipientsProvider;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccessMock;

import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RecipientsProviderTranslatorImplTest {

    private RecipientsProviderTranslatorImpl translator;

    private NationStatesMock nationStatesMock;
    private RegionDumpAccessMock accessMock;

    @Before
    public void setUp() {
        nationStatesMock = new NationStatesMock();
        accessMock = new RegionDumpAccessMock();
        translator = new RecipientsProviderTranslatorImpl(nationStatesMock, accessMock);
    }

    @After
    public void tearDown() {
        nationStatesMock = null;
        accessMock = null;
        translator = null;
    }

    @Test
    public void testFromProvider_null() {
        System.out.println("fromProvider should return emptry string on null");

        // Arrange
        final RecipientsProvider provider = null;

        // Act
        final String stringified = translator.fromProvider(provider);

        // Assert
        assertEquals("", stringified);
    }

    @Test
    public void testFromProvider_nullprovider() {
        System.out.println("fromProvider should return emptry string on null-provider");

        // Arrange
        final RecipientsProvider provider = new NullRecipientsProvider();

        // Act
        final String stringified = translator.fromProvider(provider);

        // Assert
        assertEquals("", stringified);
    }

    @Test
    public void testFromProvider_emptyNations() {
        System.out.println("fromProvider should return 'NATIONS[]' on empty NationsProvider");

        // Arrange
        final RecipientsProvider provider = new NationsProvider(new HashSet<>());

        // Act
        final String stringified = translator.fromProvider(provider);

        // Assert
        assertEquals("NATIONS[]", stringified);
    }

    @Test
    public void testFromProvider_Nations() {
        System.out.println("fromProvider should return 'NATIONS[agadar]' on NationsProvider with 1 nation");

        // Arrange
        final HashSet<String> nations = new HashSet<>();
        nations.add("agadar");
        final RecipientsProvider provider = new NationsProvider(nations);

        // Act
        final String stringified = translator.fromProvider(provider);

        // Assert
        assertEquals("NATIONS[agadar]", stringified);
    }

    @Test
    public void testFromProvider_Nations2() {
        System.out.println("fromProvider should return 'NATIONS[agadar, vancouvia]' on NationsProvider with 2 nations");

        // Arrange
        final HashSet<String> nations = new HashSet<>();
        nations.add("agadar");
        nations.add("vancouvia");
        final RecipientsProvider provider = new NationsProvider(nations);

        // Act
        final String stringified = translator.fromProvider(provider);

        // Assert
        assertEquals("NATIONS[agadar, vancouvia]", stringified);
    }

}
