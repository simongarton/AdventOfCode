package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.year2024.day21.DirectionalKeypad;
import com.simongarton.adventofcode.year2024.day21.Keypad;
import com.simongarton.adventofcode.year2024.day21.NumericKeypad;
import com.simongarton.adventofcode.year2024.day21.Program;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.simongarton.adventofcode.year2024.Year2024Day21FiguringOutProblem.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Year2024Day21FiguringOutProblemTest {

    @Test
    void testDirectionalOnly() {

        // given
        final NumericKeypad main = new NumericKeypad("main");
        final List<String> keyPresses = List.of(UP, LEFT, LEFT, ACTIVATE, UP, RIGHT, UP, RIGHT, ACTIVATE);

        // when
        keyPresses.forEach(main::press);

        // then
        assertEquals(List.of("1", "9"), main.getKeysPressed());
    }

    @Test
    void twoTwoKeypads() {

        // given
        final NumericKeypad main = new NumericKeypad("main");
        final DirectionalKeypad robot1 = new DirectionalKeypad("robot1", null, main);
        final List<String> keyPresses = List.of(LEFT, ACTIVATE, DOWN, LEFT, ACTIVATE, ACTIVATE, RIGHT, RIGHT, UP, ACTIVATE,
                LEFT, ACTIVATE, ACTIVATE, DOWN, RIGHT, ACTIVATE, ACTIVATE, UP, ACTIVATE);

        // when
        keyPresses.forEach(robot1::press);

        // then
        assertEquals(List.of("1", "9"), main.getKeysPressed());
    }

    @Test
    void integrationTest() {

        // given
        final NumericKeypad main = new NumericKeypad("main");
        final DirectionalKeypad robot1 = new DirectionalKeypad("robot1", null, main);

        final Map<Keypad, String> status = new HashMap<>();
        status.put(main, "A");
        status.put(robot1, "A");

        final List<String> commandsNeeded = List.of("1");

        // when
        final Program program = main.getProgramFor(commandsNeeded, status);

        // then
        assertNotNull(program);


    }
}
