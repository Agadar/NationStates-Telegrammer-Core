package com.github.agadar.telegrammer.core.recipients.provider;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.DefaultNationStatesImpl;
import com.github.agadar.nationstates.domain.common.happening.EjectedHappening;
import com.github.agadar.nationstates.domain.common.happening.Happening;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.query.WorldQuery;
import com.github.agadar.nationstates.shard.WorldShard;

/**
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class EjectedNationsProviderTest {

    private WorldQuery worldQueryMock;
    private NationStates nationStatesMock;
    private EjectedNationsProvider provider;
    private World world;

    @Before
    public void setUp() {
        world = new World();
        world.setHappenings(new ArrayList<>());
        world.getHappenings()
                .add(new EjectedHappening(193257689L, 1520092742L,
                        "@@demorlan_goricky@@ was ejected from %%canterbury%% by @@new_legland@@.", "new_legland",
                        "demorlan_goricky", true, "canterbury"));
        world.getHappenings().add(new Happening(193260412L, 1520094314L,
                "@@d-community@@ changed its national motto to \"Abrir todas las jaulas\"."));

        worldQueryMock = Mockito.mock(WorldQuery.class);
        Mockito.when(worldQueryMock.execute()).thenReturn(world);

        nationStatesMock = Mockito.mock(DefaultNationStatesImpl.class);
        Mockito.when(nationStatesMock.getWorld(WorldShard.HAPPENINGS)).thenReturn(worldQueryMock);

        provider = new EjectedNationsProvider(nationStatesMock);
    }

    @Test
    public void testGetRecipients() {
        System.out.println("testGetRecipients");

        // Act
        var ejectedNations = provider.getRecipients();

        // Assert
        Assert.assertEquals(1, ejectedNations.size());
        Assert.assertEquals("demorlan_goricky", ejectedNations.iterator().next());
    }

}
