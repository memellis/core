package com.ellzone.component;

import com.ellzone.slotpuzzle2d.components.NameComponent;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.entities.Entities;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestComponent {

    public static final String PLAYER_NAME1 = "testName1";
    public static final String PLAYER_NAME2 = "testName2";
    public static final float X_POSITION = 10.0f;
    public static final float Y_POSITION = 10.0f;
    private Player player;

    @Before
    public void setUp() {
        player = new Player();
        player.initialise();
    }

    @After
    public void tearDown() {
        player = null;
    }

    @Test
    public void testComponent() {
        player.getComponents(PositionComponent.class).x = X_POSITION;
        player.getComponents(PositionComponent.class).y = Y_POSITION;
        player.getComponents(NameComponent.class).name = PLAYER_NAME1;
        assertThat(player.getComponents(PositionComponent.class).x, CoreMatchers.<Float>is(equalTo(10.0f)));
        assertThat(player.getComponents(PositionComponent.class).y, CoreMatchers.<Float>is(equalTo(10.0f)));
        assertThat(player.getComponents(NameComponent.class).name, is(equalTo(PLAYER_NAME1)));
    }

    @Test
    public void testComponentAddComponentById() {
        NameComponent nameComponent = new NameComponent();
        nameComponent.name = PLAYER_NAME2;
        Entities.addComponents(player.id, nameComponent);
        assertThat(player.getComponents(NameComponent.class).name, is(equalTo(PLAYER_NAME2)));
    }

    @Test
    public void testCompenentHasComponent() {
        assertThat(player.hasComponents(NameComponent.class), is(true));
        assertThat(player.hasComponents(Player.class), is(false));
    }

    @Test
    public void testCompenentHasComponentById() {
        assertThat(Entities.hasComponents(player.id, NameComponent.class), is(true));
        assertThat(Entities.hasComponents(player.id, Player.class), is(false));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testComponentIllegalArgumentException() {
        player.getComponents(Player.class);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testComponentByIdIllegalArgumentExceptionCausedByComponentoesNotExist() {
        player.getComponents(player.id, Player.class);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testComponentByIdIllegalArgumentExceptionCausedByNoIdDoesNotExist() {
        player.getComponents(null, PositionComponent.class);
    }

    public class Player extends Entities {
        public Player() {
            super();
        }

        public void initialise() {
            PositionComponent positionComponent = new PositionComponent();
            addComponents(positionComponent);
            NameComponent nameComponent = new NameComponent();
            addComponents(nameComponent);
        }
    }
}
