package com.simongarton.adventofcode.year2024;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.simongarton.adventofcode.year2024.Year2024Day21.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Year2024Day21Test {

    @Test
    void testDirectionalOnly() {

        // given
        final Year2024Day21.NumericKeypad main = new Year2024Day21.NumericKeypad("main");
        final List<String> keyPresses = List.of(UP, LEFT, LEFT, ACTIVATE, UP, RIGHT, UP, RIGHT, ACTIVATE);

        // when
        keyPresses.forEach(main::press);

        // then
        assertEquals(List.of("1", "9"), main.keysPressed);
    }

    @Test
    void twoTwoKeypads() {

        // given
        final Year2024Day21.NumericKeypad main = new Year2024Day21.NumericKeypad("main");
        final Year2024Day21.DirectionalKeypad robot1 = new DirectionalKeypad("robot1", null, main);
        final List<String> keyPresses = List.of(LEFT, ACTIVATE, DOWN, LEFT, ACTIVATE, ACTIVATE, RIGHT, RIGHT, UP, ACTIVATE,
                LEFT, ACTIVATE, ACTIVATE, DOWN, RIGHT, ACTIVATE, ACTIVATE, UP, ACTIVATE);

        // when
        keyPresses.forEach(robot1::press);

        // then
        assertEquals(List.of("1", "9"), main.keysPressed);
    }
}
