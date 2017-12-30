package com.github.agadar.telegrammer.core.recipients.translator;

import com.github.agadar.nationstates.NationStatesMock;
import com.github.agadar.telegrammer.core.nationdumpaccess.NationDumpAccessMock;

import com.github.agadar.telegrammer.core.recipients.provider.IRecipientsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NationsProvider;
import com.github.agadar.telegrammer.core.recipients.provider.NullRecipientsProvider;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RecipientsProviderTranslatorTest {

    private RecipientsProviderTranslator translator;

    private NationStatesMock nationStatesMock;
    private NationDumpAccessMock accessMock;

    @Before
    public void setUp() {
        nationStatesMock = new NationStatesMock();
        accessMock = new NationDumpAccessMock();
        translator = new RecipientsProviderTranslator(nationStatesMock, accessMock);
    }

    @After
    public void tearDown() {
        nationStatesMock = null;
        accessMock = null;
        translator = null;
    }

    @Test
    public void testToProvider_null() {
        System.out.println("ToProvider should return null-provider on null string");

        // Act
        final IRecipientsProvider provider = translator.toProvider(null);

        // Assert
        assertTrue(provider instanceof NullRecipientsProvider);
    }

    @Test
    public void testToProvider_empty() {
        System.out.println("ToProvider should return null-provider on empty string");

        // Act
        final IRecipientsProvider provider = translator.toProvider("");

        // Assert
        assertTrue(provider instanceof NullRecipientsProvider);
    }

    @Test
    public void testToProvider_invalid() {
        System.out.println("ToProvider should return null-provider on invalid string");

        // Act
        final IRecipientsProvider provider = translator.toProvider("someInvalidString");

        // Assert
        assertTrue(provider instanceof NullRecipientsProvider);
    }

    @Test
    public void testToProvider_emptyNations() {
        System.out.println("ToProvider should return empty NationsProvider on 'NATIONS'");

        // Act
        final IRecipientsProvider provider = translator.toProvider("NATIONS");

        // Assert
        assertTrue(provider instanceof NationsProvider);
        final NationsProvider nationsProvider = (NationsProvider) provider;
        assertEquals(nationsProvider.nations.size(), 0);
    }

    @Test
    public void testToProvider_emptyNations2() {
        System.out.println("ToProvider should return empty NationsProvider on 'NATIONS[]'");

        // Act
        final IRecipientsProvider provider = translator.toProvider("NATIONS[]");

        // Assert
        assertTrue(provider instanceof NationsProvider);
        final NationsProvider nationsProvider = (NationsProvider) provider;
        assertEquals(nationsProvider.nations.size(), 0);
    }

    @Test
    public void testToProvider_Nations() {
        System.out.println("ToProvider should return NationsProvider on 'NATIONS[agadar]'");

        // Act
        final IRecipientsProvider provider = translator.toProvider("NATIONS[agadar]");

        // Assert
        assertTrue(provider instanceof NationsProvider);
        final NationsProvider nationsProvider = (NationsProvider) provider;
        assertEquals(nationsProvider.nations.size(), 1);
        assertEquals(nationsProvider.nations.toString(), "[agadar]");
    }

    @Test
    public void testToProvider_Nations2() {
        System.out.println("ToProvider should return NationsProvider on 'NATIONS[agadar,vancouvia]'");

        // Act
        final IRecipientsProvider provider = translator.toProvider("NATIONS[agadar,vancouvia]");

        // Assert
        assertTrue(provider instanceof NationsProvider);
        final NationsProvider nationsProvider = (NationsProvider) provider;
        assertEquals(nationsProvider.nations.size(), 2);
        assertEquals(nationsProvider.nations.toString(), "[agadar, vancouvia]");
    }

    @Test
    public void testFromProvider_null() {
        System.out.println("fromProvider should return emptry string on null");

        // Arrange
        final IRecipientsProvider provider = null;

        // Act
        final String stringified = translator.fromProvider(provider);

        // Assert
        assertEquals("", stringified);
    }

    @Test
    public void testFromProvider_nullprovider() {
        System.out.println("fromProvider should return emptry string on null-provider");

        // Arrange
        final IRecipientsProvider provider = new NullRecipientsProvider();

        // Act
        final String stringified = translator.fromProvider(provider);

        // Assert
        assertEquals("", stringified);
    }

    @Test
    public void testFromProvider_emptyNations() {
        System.out.println("fromProvider should return 'NATIONS[]' on empty NationsProvider");

        // Arrange
        final IRecipientsProvider provider = new NationsProvider(new HashSet<>());

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
        final IRecipientsProvider provider = new NationsProvider(nations);

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
        final IRecipientsProvider provider = new NationsProvider(nations);

        // Act
        final String stringified = translator.fromProvider(provider);

        // Assert
        assertEquals("NATIONS[agadar, vancouvia]", stringified);
    }

}
