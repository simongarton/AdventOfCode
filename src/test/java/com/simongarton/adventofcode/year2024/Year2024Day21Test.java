package com.simongarton.adventofcode.year2024;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Year2024Day21Test {

    @ParameterizedTest
    @MethodSource("sampleSequences")
    void fullSequences(final String code, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final String actual = year2024Day21.fullSequence(code);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void buildStateForNumpad() {

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
        final Year2024Day21.State actual = year2024Day21.buildStateForNumpad(from, to);

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

    @ParameterizedTest
    @MethodSource("dirpadMovement")
    void buildStateForDirpad(final String from, final String to, final String expectedPresses) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final Year2024Day21.State expected = new Year2024Day21.State();
        expected.initialLocation = from;
        expected.finalLocation = to;
        expected.requiredPress = to; // this may / may not be redundant ?
        expected.presses = expectedPresses;

        // when
        final Year2024Day21.State actual = year2024Day21.buildStateForDirpad(from, to);

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

    static List<Arguments> dirpadMovement() {

        return List.of(
                Arguments.of("A", "^", "<A"),
                Arguments.of("A", "<", "v<<A"),
                Arguments.of("<", "A", ">>^A"),
                Arguments.of("v", "^", "^A"),
                Arguments.of("^", ">", "v>A")
        );
    }

    static List<Arguments> sampleSequences() {

        return List.of(
                Arguments.of("029A", "<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A"),
                Arguments.of("980A", "<v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A"),
                Arguments.of("179A", "<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A"),
                Arguments.of("456A", "<v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A"),
                Arguments.of("379A", "<v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A")
        );
    }
}
