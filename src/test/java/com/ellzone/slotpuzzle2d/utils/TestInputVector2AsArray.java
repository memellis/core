package com.ellzone.slotpuzzle2d.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestInputVector2AsArray {

    @Test
    public void testOneVector2() {
        InputVector2AsArray inputVector2AsArray = new InputVector2AsArray("10 10");
        Array<Vector2> inputVector2s = inputVector2AsArray.readVector2s();
        assertThat(inputVector2s.get(0), is(equalTo(new Vector2(10, 10))));
    }

    @Test
    public void moreThanOneVector2() {
        InputVector2AsArray inputVector2AsArray = new InputVector2AsArray("10 10 20 20");
        Array<Vector2> inputVector2s = inputVector2AsArray.readVector2s();
        assertThat(inputVector2s.get(0), is(equalTo(new Vector2(10, 10))));
        assertThat(inputVector2s.get(1), is(equalTo(new Vector2(20, 20))));
    }

    @Test
    public void emptyInput() {
        InputVector2AsArray inputVector2AsArray = new InputVector2AsArray("");
        Array<Vector2> inputVector2s = inputVector2AsArray.readVector2s();
        assertThat(inputVector2s.size, is(equalTo(0)));
    }

    @Test(expected = NoSuchElementException.class)
    public void incompleteVector() {
        InputVector2AsArray inputVector2AsArray = new InputVector2AsArray("10");
        Array<Vector2> inputVector2s = inputVector2AsArray.readVector2s();
    }
}
