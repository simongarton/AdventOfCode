package com.simongarton.adventofcode.year2024;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Year2024Day21Test {

    @Test
    void fullSequence() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String code = "029A";
        final String expected = "<A^A^^>AvvvA";

        // when
        final String actual = year2024Day21.fullSequence(code);

        // then
        assertEquals(expected, actual);

    }

    @Test
    void buildStateForNumber() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String from = "3";
        final String to = "4";
        final Year2024Day21.State expected = new Year2024Day21.State();
        expected.initialLocation = from;
        expected.finalLocation = to;
        expected.requiredPress = to; // this may / may not be redundant ?
        expected.presses = "^<<A";

        // when
        final Year2024Day21.State actual = year2024Day21.buildStateForNumber(from, to);

        // then
        assertEquals(expected, actual);

    }

    @ParameterizedTest
    @MethodSource("numberMovement")
    void buildPressesForNumberMovement(final String from, final String to, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final String actual = year2024Day21.buildPressesForNumberMovement(from, to);

        // then
        assertEquals(expected, actual);
    }

    static List<Arguments> numberMovement() {

        return List.of(
                Arguments.of("A", "3", "^A"),
                Arguments.of("A", "0", "<A"),
                Arguments.of("A", "2", "^<A"),
                Arguments.of("7", "A", ">>vvvA"),
                Arguments.of("4", "9", "^>>A"),
                Arguments.of("8", "1", "<vvA"),
                Arguments.of("A", "1", "^<<A"),
                Arguments.of("1", "A", ">>vA")
        );
    }
}